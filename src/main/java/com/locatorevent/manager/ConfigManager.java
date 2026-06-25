package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private final LocatorEvent plugin;
    private FileConfiguration config;

    // Cached values
    private int minCooldownHours;
    private int maxCooldownHours;
    private int minEventDurationMinutes;
    private int maxEventDurationMinutes;
    private String worldMode;
    private Set<String> worldList;
    private boolean locatorEnabled;
    private boolean bossBarEnabled;
    private String bossBarText;
    private String bossBarColor;
    private String bossBarStyle;
    private int bossBarUpdateInterval;
    private String eventStartMessage;
    private String eventEndMessage;
    private String preAnnouncementMessage;
    private int preAnnouncementLeadMinutes;
    private boolean particlesEnabled;
    private String particleType;
    private int particleAmount;
    private boolean titlesEnabled;
    private String startTitle;
    private String startSubtitle;
    private String endTitle;
    private String endSubtitle;
    private boolean soundsEnabled;
    private String startSoundType;
    private double startSoundVolume;
    private double startSoundPitch;
    private String endSoundType;
    private double endSoundVolume;
    private double endSoundPitch;

    public ConfigManager(LocatorEvent plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        // Event Timing
        this.minCooldownHours = config.getInt("event.timing.minCooldownHours", 1);
        this.maxCooldownHours = config.getInt("event.timing.maxCooldownHours", 3);
        this.minEventDurationMinutes = config.getInt("event.timing.minEventDurationMinutes", 10);
        this.maxEventDurationMinutes = config.getInt("event.timing.maxEventDurationMinutes", 15);

        // World settings
        this.worldMode = config.getString("event.worlds.mode", "WHITELIST");
        this.worldList = Collections.unmodifiableSet(new HashSet<>(config.getStringList("event.worlds.list")));
        this.locatorEnabled = config.getBoolean("event.enableLocator", true);

        // BossBar settings
        this.bossBarEnabled = config.getBoolean("bossbar.enabled", true);
        this.bossBarText = config.getString("bossbar.text", "&b&lLocator Event Active! &7Time Left: &e%locatorevent_time_left%");
        this.bossBarColor = config.getString("bossbar.color", "BLUE");
        this.bossBarStyle = config.getString("bossbar.style", "SOLID");
        this.bossBarUpdateInterval = config.getInt("bossbar.updateIntervalTicks", 20);

        // Messages
        this.eventStartMessage = config.getString("messages.eventStart");
        this.eventEndMessage = config.getString("messages.eventEnd");
        this.preAnnouncementMessage = config.getString("messages.preAnnouncement");
        this.preAnnouncementLeadMinutes = config.getInt("messages.preAnnouncementLeadMinutes", 5);

        // Effects - Particles
        this.particlesEnabled = config.getBoolean("effects.particles.enabled", true);
        this.particleType = config.getString("effects.particles.type", "HAPPY_VILLAGER");
        this.particleAmount = config.getInt("effects.particles.amount", 5);

        // Effects - Titles
        this.titlesEnabled = config.getBoolean("effects.titles.enabled", true);
        this.startTitle = config.getString("effects.titles.start.title");
        this.startSubtitle = config.getString("effects.titles.start.subtitle");
        this.endTitle = config.getString("effects.titles.end.title");
        this.endSubtitle = config.getString("effects.titles.end.subtitle");

        // Effects - Sounds
        this.soundsEnabled = config.getBoolean("effects.sounds.enabled", true);
        this.startSoundType = config.getString("effects.sounds.start.type", "ENTITY_EXPERIENCE_ORB_PICKUP");
        this.startSoundVolume = config.getDouble("effects.sounds.start.volume", 1.0);
        this.startSoundPitch = config.getDouble("effects.sounds.start.pitch", 1.0);
        this.endSoundType = config.getString("effects.sounds.end.type", "ENTITY_WITHER_SPAWN");
        this.endSoundVolume = config.getDouble("effects.sounds.end.volume", 0.5);
        this.endSoundPitch = config.getDouble("effects.sounds.end.pitch", 1.0);
    }

    // Event Timing
    public int getMinCooldownHours() { return minCooldownHours; }
    public int getMaxCooldownHours() { return maxCooldownHours; }
    public int getMinEventDurationMinutes() { return minEventDurationMinutes; }
    public int getMaxEventDurationMinutes() { return maxEventDurationMinutes; }

    // World settings
    public String getWorldMode() { return worldMode; }
    public Set<String> getWorldList() { return worldList; }
    public boolean isLocatorEnabled() { return locatorEnabled; }

    // BossBar settings
    public boolean isBossBarEnabled() { return bossBarEnabled; }
    public String getBossBarText() { return bossBarText; }
    public String getBossBarColor() { return bossBarColor; }
    public String getBossBarStyle() { return bossBarStyle; }
    public int getBossBarUpdateInterval() { return bossBarUpdateInterval; }

    // Messages
    public String getEventStartMessage() { return eventStartMessage; }
    public String getEventEndMessage() { return eventEndMessage; }
    public String getPreAnnouncementMessage() { return preAnnouncementMessage; }
    public int getPreAnnouncementLeadMinutes() { return preAnnouncementLeadMinutes; }

    // Effects - Particles
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public String getParticleType() { return particleType; }
    public int getParticleAmount() { return particleAmount; }

    // Effects - Titles
    public boolean isTitlesEnabled() { return titlesEnabled; }
    public String getStartTitle() { return startTitle; }
    public String getStartSubtitle() { return startSubtitle; }
    public String getEndTitle() { return endTitle; }
    public String getEndSubtitle() { return endSubtitle; }

    // Effects - Sounds
    public boolean isSoundsEnabled() { return soundsEnabled; }
    public String getStartSoundType() { return startSoundType; }
    public double getStartSoundVolume() { return startSoundVolume; }
    public double getStartSoundPitch() { return startSoundPitch; }
    public String getEndSoundType() { return endSoundType; }
    public double getEndSoundVolume() { return endSoundVolume; }
    public double getEndSoundPitch() { return endSoundPitch; }
}
