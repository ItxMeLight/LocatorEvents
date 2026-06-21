package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private final LocatorEvent plugin;

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
    private BossBar.Color bossBarColor;
    private BossBar.Overlay bossBarStyle;
    private int bossBarUpdateInterval;

    private String eventStartMessage;
    private String eventEndMessage;
    private String preAnnouncementMessage;
    private int preAnnouncementLeadMinutes;

    private boolean particlesEnabled;
    private Particle particleType;
    private int particleAmount;

    private boolean titlesEnabled;
    private String startTitle;
    private String startSubtitle;
    private String endTitle;
    private String endSubtitle;

    private boolean soundsEnabled;
    private Sound startSoundType;
    private double startSoundVolume;
    private double startSoundPitch;
    private Sound endSoundType;
    private double endSoundVolume;
    private double endSoundPitch;

    public ConfigManager(LocatorEvent plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Event Timing
        minCooldownHours = config.getInt("event.timing.minCooldownHours", 1);
        maxCooldownHours = config.getInt("event.timing.maxCooldownHours", 3);
        minEventDurationMinutes = config.getInt("event.timing.minEventDurationMinutes", 10);
        maxEventDurationMinutes = config.getInt("event.timing.maxEventDurationMinutes", 15);

        // World settings
        worldMode = config.getString("event.worlds.mode", "WHITELIST");
        worldList = new HashSet<>(config.getStringList("event.worlds.list"));
        locatorEnabled = config.getBoolean("event.enableLocator", true);

        // BossBar settings
        bossBarEnabled = config.getBoolean("bossbar.enabled", true);
        bossBarText = config.getString("bossbar.text", "&b&lLocator Event Active! &7Time Left: &e%locatorevent_time_left%");

        try {
            bossBarColor = BossBar.Color.valueOf(config.getString("bossbar.color", "BLUE").toUpperCase());
        } catch (Exception e) {
            bossBarColor = BossBar.Color.BLUE;
        }

        try {
            bossBarStyle = BossBar.Overlay.valueOf(config.getString("bossbar.style", "PROGRESS").toUpperCase());
        } catch (Exception e) {
            bossBarStyle = BossBar.Overlay.PROGRESS;
        }

        bossBarUpdateInterval = config.getInt("bossbar.updateIntervalTicks", 20);

        // Messages
        eventStartMessage = config.getString("messages.eventStart");
        eventEndMessage = config.getString("messages.eventEnd");
        preAnnouncementMessage = config.getString("messages.preAnnouncement");
        preAnnouncementLeadMinutes = config.getInt("messages.preAnnouncementLeadMinutes", 5);

        // Effects - Particles
        particlesEnabled = config.getBoolean("effects.particles.enabled", true);
        try {
            particleType = Particle.valueOf(config.getString("effects.particles.type", "HAPPY_VILLAGER").toUpperCase());
        } catch (Exception e) {
            particleType = Particle.HAPPY_VILLAGER;
        }
        particleAmount = config.getInt("effects.particles.amount", 5);

        // Effects - Titles
        titlesEnabled = config.getBoolean("effects.titles.enabled", true);
        startTitle = config.getString("effects.titles.start.title");
        startSubtitle = config.getString("effects.titles.start.subtitle");
        endTitle = config.getString("effects.titles.end.title");
        endSubtitle = config.getString("effects.titles.end.subtitle");

        // Effects - Sounds
        soundsEnabled = config.getBoolean("effects.sounds.enabled", true);
        try {
            startSoundType = Sound.valueOf(config.getString("effects.sounds.start.type", "ENTITY_EXPERIENCE_ORB_PICKUP").toUpperCase());
        } catch (Exception e) {
            startSoundType = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
        startSoundVolume = config.getDouble("effects.sounds.start.volume", 1.0);
        startSoundPitch = config.getDouble("effects.sounds.start.pitch", 1.0);

        try {
            endSoundType = Sound.valueOf(config.getString("effects.sounds.end.type", "ENTITY_WITHER_SPAWN").toUpperCase());
        } catch (Exception e) {
            endSoundType = Sound.ENTITY_WITHER_SPAWN;
        }
        endSoundVolume = config.getDouble("effects.sounds.end.volume", 0.5);
        endSoundPitch = config.getDouble("effects.sounds.end.pitch", 1.0);
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
    public BossBar.Color getBossBarColor() { return bossBarColor; }
    public BossBar.Overlay getBossBarStyle() { return bossBarStyle; }
    public int getBossBarUpdateInterval() { return bossBarUpdateInterval; }

    // Messages
    public String getEventStartMessage() { return eventStartMessage; }
    public String getEventEndMessage() { return eventEndMessage; }
    public String getPreAnnouncementMessage() { return preAnnouncementMessage; }
    public int getPreAnnouncementLeadMinutes() { return preAnnouncementLeadMinutes; }

    // Effects - Particles
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public Particle getParticleType() { return particleType; }
    public int getParticleAmount() { return particleAmount; }

    // Effects - Titles
    public boolean isTitlesEnabled() { return titlesEnabled; }
    public String getStartTitle() { return startTitle; }
    public String getStartSubtitle() { return startSubtitle; }
    public String getEndTitle() { return endTitle; }
    public String getEndSubtitle() { return endSubtitle; }

    // Effects - Sounds
    public boolean isSoundsEnabled() { return soundsEnabled; }
    public Sound getStartSoundType() { return startSoundType; }
    public double getStartSoundVolume() { return startSoundVolume; }
    public double getStartSoundPitch() { return startSoundPitch; }
    public Sound getEndSoundType() { return endSoundType; }
    public double getEndSoundVolume() { return endSoundVolume; }
    public double getEndSoundPitch() { return endSoundPitch; }
}
