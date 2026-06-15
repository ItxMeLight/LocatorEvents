package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
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

public class EventManager {

    private final LocatorEvent plugin;
    private final Random random = new Random();

    private EventState state = EventState.INACTIVE;
    private long timeLeftSeconds = 0;
    private long totalDurationSeconds = 0;

    private BukkitTask currentTask;

    public enum EventState {
        ACTIVE, INACTIVE
    }

    public EventManager(LocatorEvent plugin) {
        this.plugin = plugin;
    }

    public void startScheduler() {
        scheduleNextEvent();
    }

    private void scheduleNextEvent() {
        cancelCurrentTask();
        ConfigManager config = plugin.getConfigManager();
        int minHours = config.getMinCooldownHours();
        int maxHours = config.getMaxCooldownHours();

        long delayHours = minHours + (maxHours > minHours ? random.nextInt(maxHours - minHours + 1) : 0);
        long delayTicks = delayHours * 60 * 60 * 20;

        int announceLeadMinutes = config.getPreAnnouncementLeadMinutes();
        long announceLeadTicks = (long) announceLeadMinutes * 60 * 20;

        if (delayTicks > announceLeadTicks) {
            currentTask = new BukkitRunnable() {
                @Override
                public void run() {
                    broadcastAnnouncement(announceLeadMinutes + " minutes");

                    currentTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            startEvent();
                        }
                    }.runTaskLater(plugin, announceLeadTicks);
                }
            }.runTaskLater(plugin, delayTicks - announceLeadTicks);
        } else {
            currentTask = new BukkitRunnable() {
                @Override
                public void run() {
                    startEvent();
                }
            }.runTaskLater(plugin, delayTicks);
        }

        plugin.getLogger().info("Next Locator Event scheduled in " + delayHours + " hours.");
    }

    public void startEvent() {
        if (state == EventState.ACTIVE) return;
        cancelCurrentTask();

        ConfigManager config = plugin.getConfigManager();
        int minMinutes = config.getMinEventDurationMinutes();
        int maxMinutes = config.getMaxEventDurationMinutes();

        int durationMinutes = minMinutes + (maxMinutes > minMinutes ? random.nextInt(maxMinutes - minMinutes + 1) : 0);
        totalDurationSeconds = (long) durationMinutes * 60;
        timeLeftSeconds = totalDurationSeconds;
        state = EventState.ACTIVE;

        applyLocatorVisibility(true);
        plugin.getBossBarManager().show();
        broadcastStart();

        currentTask = new BukkitRunnable() {
            int ticksElapsed = 0;
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
                    applyLocatorVisibility(true);
                }

                if (config.isParticlesEnabled()) {
                    spawnParticles();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
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

    private void applyLocatorVisibility(boolean visible) {
        if (!plugin.getConfigManager().isLocatorEnabled()) return;

        ConfigManager config = plugin.getConfigManager();
        String mode = config.getWorldMode();
        java.util.List<String> worldList = config.getWorldList();

        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            boolean inList = worldList.contains(world.getName());
            boolean shouldApply = (mode.equalsIgnoreCase("WHITELIST") && inList) ||
                                 (mode.equalsIgnoreCase("BLACKLIST") && !inList);

            if (shouldApply) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType().name().contains("FILLED_MAP")) {
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
            }
        }
    }

    private void broadcastAnnouncement(String time) {
        String msg = plugin.getConfigManager().getPreAnnouncementMessage();
        if (msg != null && !msg.isEmpty()) {
            msg = msg.replace("%time%", time);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(parseText(msg, player));
            }
        }
    }

    private void spawnParticles() {
        ConfigManager config = plugin.getConfigManager();
        try {
            Particle particle = Particle.valueOf(config.getParticleType().toUpperCase());
            int amount = config.getParticleAmount();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getWorld().spawnParticle(particle, player.getLocation().add(0, 2, 0), amount, 0.5, 0.5, 0.5, 0.05);
            }
        } catch (Exception ignored) {}
    }

    private void broadcastStart() {
        ConfigManager config = plugin.getConfigManager();
        String msg = config.getEventStartMessage();

        boolean titlesEnabled = config.isTitlesEnabled();
        Component startTitle = parseText(config.getStartTitle(), null); // Parsed later for each player if needed, but Title can be shared if no player placeholders.
        Component startSubtitle = parseText(config.getStartSubtitle(), null);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (msg != null && !msg.isEmpty()) {
                player.sendMessage(parseText(msg, player));
            }
            if (titlesEnabled) {
                player.showTitle(Title.title(parseText(config.getStartTitle(), player), parseText(config.getStartSubtitle(), player)));
            }
            if (config.isSoundsEnabled()) {
                try {
                    Sound sound = Sound.valueOf(config.getStartSoundType().toUpperCase());
                    player.playSound(player.getLocation(), sound, (float)config.getStartSoundVolume(), (float)config.getStartSoundPitch());
                } catch (Exception ignored) {}
            }
        }
    }

    private void broadcastEnd() {
        ConfigManager config = plugin.getConfigManager();
        String msg = config.getEventEndMessage();
        boolean titlesEnabled = config.isTitlesEnabled();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (msg != null && !msg.isEmpty()) {
                player.sendMessage(parseText(msg, player));
            }
            if (titlesEnabled) {
                player.showTitle(Title.title(parseText(config.getEndTitle(), player), parseText(config.getEndSubtitle(), player)));
            }
            if (config.isSoundsEnabled()) {
                try {
                    Sound sound = Sound.valueOf(config.getEndSoundType().toUpperCase());
                    player.playSound(player.getLocation(), sound, (float)config.getEndSoundVolume(), (float)config.getEndSoundPitch());
                } catch (Exception ignored) {}
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
