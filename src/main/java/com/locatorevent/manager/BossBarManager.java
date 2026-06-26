package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private final LocatorEvent plugin;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    private boolean active = false;

    // Cached resources
    private BossBar.Color cachedColor;
    private BossBar.Overlay cachedOverlay;

    public BossBarManager(LocatorEvent plugin) {
        this.plugin = plugin;
        this.cacheResources();
    }

    public void cacheResources() {
        ConfigManager config = plugin.getConfigManager();
        try {
            this.cachedColor = BossBar.Color.valueOf(config.getBossBarColor().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.cachedColor = BossBar.Color.BLUE;
        }

        try {
            this.cachedOverlay = BossBar.Overlay.valueOf(config.getBossBarStyle().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.cachedOverlay = BossBar.Overlay.PROGRESS;
        }
    }

    public void show() {
        active = true;
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
        update();
    }

    public void hide() {
        active = false;
        playerBossBars.forEach((uuid, bar) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.hideBossBar(bar);
            }
        });
        playerBossBars.clear();
    }

    public void update() {
        if (!active) return;

        ConfigManager config = plugin.getConfigManager();
        long timeLeft = plugin.getEventManager().getTimeLeftSeconds();
        long totalDuration = plugin.getEventManager().getTotalDurationSeconds();
        float progress = totalDuration > 0 ? (float) timeLeft / totalDuration : 0;
        float finalProgress = Math.max(0.0f, Math.min(1.0f, progress));

        String rawText = config.getBossBarText();

        for (Player player : Bukkit.getOnlinePlayers()) {
            BossBar bar = playerBossBars.get(player.getUniqueId());
            if (bar == null) {
                addPlayer(player);
                bar = playerBossBars.get(player.getUniqueId());
            }

            if (bar != null) {
                bar.progress(finalProgress);
                bar.name(parseText(rawText, player));
            }
        }
    }

    public void addPlayer(Player player) {
        if (!active || !plugin.getConfigManager().isBossBarEnabled()) return;
        if (playerBossBars.containsKey(player.getUniqueId())) return;

        BossBar bar = BossBar.bossBar(
                Component.empty(),
                1.0f,
                cachedColor,
                cachedOverlay
        );

        playerBossBars.put(player.getUniqueId(), bar);
        player.showBossBar(bar);
    }

    public void removePlayer(Player player) {
        BossBar bar = playerBossBars.remove(player.getUniqueId());
        if (bar != null) {
            player.hideBossBar(bar);
        }
    }

    private Component parseText(String text, Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
