package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Set;

public class EventManager {

    private final LocatorEvent plugin;
    private final ConfigManager config;
    private final Random random = new Random();

    private EventState state = EventState.INACTIVE;
    private long timeLeftSeconds = 0;
    private long totalDurationSeconds = 0;

    private BukkitTask currentTask;

    // Cached resources
    private Particle cachedParticle;
    private Sound cachedStartSound;
    private Sound cachedEndSound;

    public enum EventState {
        ACTIVE, INACTIVE
    }

    public EventManager(LocatorEvent plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        cacheResources();
    }

    public void cacheResources() {
        try {
            this.cachedParticle = Particle.valueOf(config.getParticleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.cachedParticle = Particle.HAPPY_VILLAGER;
        }

        try {
            this.cachedStartSound = Sound.valueOf(config.getStartSoundType().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.cachedStartSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }

        try {
            this.cachedEndSound = Sound.valueOf(config.getEndSoundType().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.cachedEndSound = Sound.ENTITY_WITHER_SPAWN;
        }
    }

    public void startScheduler() {
        scheduleNextEvent();
    }

    private void scheduleNextEvent() {
        cancelCurrentTask();
        int minHours = config.getMinCooldownHours();
        int maxHours = config.getMaxCooldownHours();

        long delayHours = minHours + (maxHours > minHours ? (long) random.nextInt(maxHours - minHours + 1) : 0);
        long delayTicks = delayHours * 60 * 60 * 20;

        int announceLeadMinutes = config.getPreAnnouncementLeadMinutes();
        long announceLeadTicks = (long) announceLeadMinutes * 60 * 20;

        if (delayTicks > announceLeadTicks) {
            currentTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                broadcastAnnouncement(announceLeadMinutes + " minutes");
                currentTask = Bukkit.getScheduler().runTaskLater(plugin, this::startEvent, announceLeadTicks);
            }, delayTicks - announceLeadTicks);
        } else {
            currentTask = Bukkit.getScheduler().runTaskLater(plugin, this::startEvent, Math.max(1L, delayTicks));
        }

        plugin.getLogger().info("Next Locator Event scheduled in " + delayHours + " hours.");
    }

    public void startEvent() {
        if (state == EventState.ACTIVE) return;
        cancelCurrentTask();

        int minMinutes = config.getMinEventDurationMinutes();
        int maxMinutes = config.getMaxEventDurationMinutes();

        int durationMinutes = minMinutes + (maxMinutes > minMinutes ? random.nextInt(maxMinutes - minMinutes + 1) : 0);
        totalDurationSeconds = (long) durationMinutes * 60;
        timeLeftSeconds = totalDurationSeconds;
        state = EventState.ACTIVE;

        applyLocatorVisibility(true);
        plugin.getBossBarManager().show();
        broadcastStart();

        currentTask = new EventUpdateTask().runTaskTimer(plugin, 1L, 1L);
    }

    private class EventUpdateTask extends BukkitRunnable {
        private int ticksElapsed = 0;

        @Override
        public void run() {
            if (timeLeftSeconds <= 0) {
                endEvent();
                return;
            }

            ticksElapsed++;
            if (ticksElapsed >= 20) {
                timeLeftSeconds--;
                ticksElapsed = 0;
            }

            if ((timeLeftSeconds * 20 + (20 - ticksElapsed)) % config.getBossBarUpdateInterval() == 0) {
                plugin.getBossBarManager().update();
            }

            if (config.isParticlesEnabled() && ticksElapsed == 0) {
                spawnParticles();
            }
        }
    }

    public void endEvent() {
        endEvent(true);
    }

    public void endEvent(boolean reschedule) {
        if (state == EventState.INACTIVE) return;
        cancelCurrentTask();

        state = EventState.INACTIVE;
        applyLocatorVisibility(false);
        plugin.getBossBarManager().hide();
        broadcastEnd();

        if (reschedule) {
            scheduleNextEvent();
        }
    }

    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public void applyLocatorVisibility(boolean visible) {
        if (!config.isLocatorEnabled()) return;

        String mode = config.getWorldMode();
        Set<String> worldList = config.getWorldList();

        // 1. Aplică regula GameRule pe lumi
        for (World world : Bukkit.getWorlds()) {
            boolean inList = worldList.contains(world.getName());
            boolean shouldApply = (mode.equalsIgnoreCase("WHITELIST") && inList) ||
                                  (mode.equalsIgnoreCase("BLACKLIST") && !inList);

            if (shouldApply) {
                try {
                    // Modern Locator Bar HUD control via GameRule
                    // We use getByName for safety across 1.21.4+ versions.
                    GameRule<?> genericRule = GameRule.getByName("locatorBar");
                    if (genericRule != null && genericRule.getType() == Boolean.class) {
                        @SuppressWarnings("unchecked")
                        GameRule<Boolean> locatorBarRule = (GameRule<Boolean>) genericRule;
                        world.setGameRule(locatorBarRule, visible);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to set locatorBar game rule in world " + world.getName() + ": " + e.getMessage());
                }
            }
        }

        // 2. Actualizează hărțile din inventarele jucătorilor
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerInventoryMaps(player, visible);
        }
    }

    public boolean isWorldEnabled(World world) {
        String mode = config.getWorldMode();
        Set<String> worldList = config.getWorldList();
        boolean inList = worldList.contains(world.getName());

        if (mode.equalsIgnoreCase("WHITELIST")) {
            return inList;
        } else if (mode.equalsIgnoreCase("BLACKLIST")) {
            return !inList;
        }
        return true;
    }

    public void updatePlayerInventoryMaps(Player player, boolean visible) {
        if (!isWorldEnabled(player.getWorld())) return;

        for (ItemStack item : player.getInventory().getContents()) {
            updateMapItem(item, visible);
        }
    }

    public void updateMapItem(ItemStack item, boolean visible) {
        if (item != null && item.getType() == Material.FILLED_MAP) {
            if (item.getItemMeta() instanceof MapMeta mapMeta) {
                if (mapMeta.hasMapView()) {
                    MapView view = mapMeta.getMapView();
                    if (view != null) {
                        view.setTrackingPosition(visible);
                        item.setItemMeta(mapMeta);
                    }
                }
            }
        }
    }

    private void broadcastAnnouncement(String time) {
        String msg = config.getPreAnnouncementMessage();
        if (msg != null && !msg.isEmpty()) {
            String finalMsg = msg.replace("%time%", time);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(parseText(finalMsg, player));
            }
        }
    }

    private void spawnParticles() {
        int amount = config.getParticleAmount();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getWorld().spawnParticle(cachedParticle, player.getLocation().add(0, 2, 0), amount, 0.5, 0.5, 0.5, 0.05);
        }
    }

    private void broadcastStart() {
        String msg = config.getEventStartMessage();
        boolean titlesEnabled = config.isTitlesEnabled();
        String startTitle = config.getStartTitle();
        String startSubtitle = config.getStartSubtitle();
        boolean soundsEnabled = config.isSoundsEnabled();
        float volume = (float) config.getStartSoundVolume();
        float pitch = (float) config.getStartSoundPitch();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (msg != null && !msg.isEmpty()) {
                player.sendMessage(parseText(msg, player));
            }
            if (titlesEnabled) {
                player.showTitle(Title.title(parseText(startTitle, player), parseText(startSubtitle, player)));
            }
            if (soundsEnabled) {
                player.playSound(player.getLocation(), cachedStartSound, volume, pitch);
            }
        }
    }

    private void broadcastEnd() {
        String msg = config.getEventEndMessage();
        boolean titlesEnabled = config.isTitlesEnabled();
        String endTitle = config.getEndTitle();
        String endSubtitle = config.getEndSubtitle();
        boolean soundsEnabled = config.isSoundsEnabled();
        float volume = (float) config.getEndSoundVolume();
        float pitch = (float) config.getEndSoundPitch();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (msg != null && !msg.isEmpty()) {
                player.sendMessage(parseText(msg, player));
            }
            if (titlesEnabled) {
                player.showTitle(Title.title(parseText(endTitle, player), parseText(endSubtitle, player)));
            }
            if (soundsEnabled) {
                player.playSound(player.getLocation(), cachedEndSound, volume, pitch);
            }
        }
    }

    private Component parseText(String text, Player player) {
        if (text == null) return Component.empty();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public EventState getState() { return state; }
    public long getTimeLeftSeconds() { return timeLeftSeconds; }
    public long getTotalDurationSeconds() { return totalDurationSeconds; }

    public String getFormattedTimeLeft() {
        long h = TimeUnit.SECONDS.toHours(timeLeftSeconds);
        long m = TimeUnit.SECONDS.toMinutes(timeLeftSeconds) % 60;
        long s = timeLeftSeconds % 60;
        if (h > 0) {
            return String.format("%02d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }
}
