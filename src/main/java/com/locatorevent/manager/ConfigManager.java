package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final LocatorEvent plugin;
    private FileConfiguration config;

    public ConfigManager(LocatorEvent plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // Event Timing
    public int getMinCooldownHours() { return config.getInt("event.timing.minCooldownHours", 1); }
    public int getMaxCooldownHours() { return config.getInt("event.timing.maxCooldownHours", 3); }
    public int getMinEventDurationMinutes() { return config.getInt("event.timing.minEventDurationMinutes", 10); }
    public int getMaxEventDurationMinutes() { return config.getInt("event.timing.maxEventDurationMinutes", 15); }

    // World settings
    public String getWorldMode() { return config.getString("event.worlds.mode", "WHITELIST"); }
    public List<String> getWorldList() { return config.getStringList("event.worlds.list"); }
    public boolean isLocatorEnabled() { return config.getBoolean("event.enableLocator", true); }

    // BossBar settings
    public boolean isBossBarEnabled() { return config.getBoolean("bossbar.enabled", true); }
    public String getBossBarText() { return config.getString("bossbar.text", "&b&lLocator Event Active! &7Time Left: &e%locatorevent_time_left%"); }
    public String getBossBarColor() { return config.getString("bossbar.color", "BLUE"); }
    public String getBossBarStyle() { return config.getString("bossbar.style", "SOLID"); }
    public int getBossBarUpdateInterval() { return config.getInt("bossbar.updateIntervalTicks", 20); }

    // Messages
    public String getEventStartMessage() { return config.getString("messages.eventStart"); }
    public String getEventEndMessage() { return config.getString("messages.eventEnd"); }
    public String getPreAnnouncementMessage() { return config.getString("messages.preAnnouncement"); }
    public int getPreAnnouncementLeadMinutes() { return config.getInt("messages.preAnnouncementLeadMinutes", 5); }

    // Effects - Particles
    public boolean isParticlesEnabled() { return config.getBoolean("effects.particles.enabled", true); }
    public String getParticleType() { return config.getString("effects.particles.type", "HAPPY_VILLAGER"); }
    public int getParticleAmount() { return config.getInt("effects.particles.amount", 5); }

    // Effects - Titles
    public boolean isTitlesEnabled() { return config.getBoolean("effects.titles.enabled", true); }
    public String getStartTitle() { return config.getString("effects.titles.start.title"); }
    public String getStartSubtitle() { return config.getString("effects.titles.start.subtitle"); }
    public String getEndTitle() { return config.getString("effects.titles.end.title"); }
    public String getEndSubtitle() { return config.getString("effects.titles.end.subtitle"); }

    // Effects - Sounds
    public boolean isSoundsEnabled() { return config.getBoolean("effects.sounds.enabled", true); }
    public String getStartSoundType() { return config.getString("effects.sounds.start.type", "ENTITY_EXPERIENCE_ORB_PICKUP"); }
    public double getStartSoundVolume() { return config.getDouble("effects.sounds.start.volume", 1.0); }
    public double getStartSoundPitch() { return config.getDouble("effects.sounds.start.pitch", 1.0); }
    public String getEndSoundType() { return config.getString("effects.sounds.end.type", "ENTITY_WITHER_SPAWN"); }
    public double getEndSoundVolume() { return config.getDouble("effects.sounds.end.volume", 0.5); }
    public double getEndSoundPitch() { return config.getDouble("effects.sounds.end.pitch", 1.0); }
}
