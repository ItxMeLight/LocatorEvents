package com.locatorevent.manager;

import com.locatorevent.LocatorEvent;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private final LocatorEvent plugin;
    private FileConfiguration config;

    // Cached fields
    private int minCooldownHours;
    private int maxCooldownHours;
    private int minEventDurationMinutes;
    private int maxEventDurationMinutes;
    private String worldMode;
    private Set<String> worldList;
    private boolean locatorEnabled;
    private boolean bossBarEnabled;
    private String bossBarText;
    private int bossBarUpdateInterval;
    private String eventStartMessage;
    private String eventEndMessage;
    private String preAnnouncementMessage;
    private int preAnnouncementLeadMinutes;
    private boolean particlesEnabled;
    private int particleAmount;
    private boolean titlesEnabled;
    private String startTitle;
    private String startSubtitle;
    private String endTitle;
    private String endSubtitle;
    private boolean soundsEnabled;
    private float startSoundVolume;
    private float startSoundPitch;
    private float endSoundVolume;
    private float endSoundPitch;

    // Cached Resource Objects
    private Particle cachedParticleType;
    private Sound cachedStartSound;
    private Sound cachedEndSound;
    private BossBar.Color cachedBossBarColor;
    private BossBar.Overlay cachedBossBarStyle;

    public ConfigManager(LocatorEvent plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.cacheValues();
    }

    private void cacheValues() {
        this.minCooldownHours = config.getInt("event.timing.minCooldownHours", 1);
        this.maxCooldownHours = config.getInt("event.timing.maxCooldownHours", 3);
        this.minEventDurationMinutes = config.getInt("event.timing.minEventDurationMinutes", 10);
        this.maxEventDurationMinutes = config.getInt("event.timing.maxEventDurationMinutes", 15);

        this.worldMode = config.getString("event.worlds.mode", "WHITELIST");
        this.worldList = Collections.unmodifiableSet(new HashSet<>(config.getStringList("event.worlds.list")));
        this.locatorEnabled = config.getBoolean("event.enableLocator", true);

        this.bossBarEnabled = config.getBoolean("bossbar.enabled", true);
        this.bossBarText = config.getString("bossbar.text", "&b&lLocator Event Active! &7Time Left: &e%locatorevent_time_left%");
        this.bossBarUpdateInterval = config.getInt("bossbar.updateIntervalTicks", 20);

        this.eventStartMessage = config.getString("messages.eventStart");
        this.eventEndMessage = config.getString("messages.eventEnd");
        this.preAnnouncementMessage = config.getString("messages.preAnnouncement");
        this.preAnnouncementLeadMinutes = config.getInt("messages.preAnnouncementLeadMinutes", 5);

        this.particlesEnabled = config.getBoolean("effects.particles.enabled", true);
        this.particleAmount = config.getInt("effects.particles.amount", 5);

        this.titlesEnabled = config.getBoolean("effects.titles.enabled", true);
        this.startTitle = config.getString("effects.titles.start.title");
        this.startSubtitle = config.getString("effects.titles.start.subtitle");
        this.endTitle = config.getString("effects.titles.end.title");
        this.endSubtitle = config.getString("effects.titles.end.subtitle");

        this.soundsEnabled = config.getBoolean("effects.sounds.enabled", true);
        this.startSoundVolume = (float) config.getDouble("effects.sounds.start.volume", 1.0);
        this.startSoundPitch = (float) config.getDouble("effects.sounds.start.pitch", 1.0);
        this.endSoundVolume = (float) config.getDouble("effects.sounds.end.volume", 0.5);
        this.endSoundPitch = (float) config.getDouble("effects.sounds.end.pitch", 1.0);

        this.cacheResources();
    }

    private void cacheResources() {
        // Cache Particle
        try {
            this.cachedParticleType = Particle.valueOf(config.getString("effects.particles.type", "HAPPY_VILLAGER").toUpperCase());
        } catch (Exception e) {
            this.cachedParticleType = Particle.HAPPY_VILLAGER;
        }

        // Cache Sounds
        try {
            this.cachedStartSound = Sound.valueOf(config.getString("effects.sounds.start.type", "ENTITY_EXPERIENCE_ORB_PICKUP").toUpperCase());
        } catch (Exception e) {
            this.cachedStartSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
        try {
            this.cachedEndSound = Sound.valueOf(config.getString("effects.sounds.end.type", "ENTITY_WITHER_SPAWN").toUpperCase());
        } catch (Exception e) {
            this.cachedEndSound = Sound.ENTITY_WITHER_SPAWN;
        }

        // Cache BossBar Color
        try {
            this.cachedBossBarColor = BossBar.Color.valueOf(config.getString("bossbar.color", "BLUE").toUpperCase());
        } catch (Exception e) {
            this.cachedBossBarColor = BossBar.Color.BLUE;
        }

        // Cache BossBar Overlay
        try {
            this.cachedBossBarStyle = BossBar.Overlay.valueOf(config.getString("bossbar.style", "PROGRESS").toUpperCase());
        } catch (Exception e) {
            this.cachedBossBarStyle = BossBar.Overlay.PROGRESS;
        }
    }

    // Getters for cached values
    public int getMinCooldownHours() { return minCooldownHours; }
    public int getMaxCooldownHours() { return maxCooldownHours; }
    public int getMinEventDurationMinutes() { return minEventDurationMinutes; }
    public int getMaxEventDurationMinutes() { return maxEventDurationMinutes; }
    public String getWorldMode() { return worldMode; }
    public Set<String> getWorldList() { return worldList; }
    public boolean isLocatorEnabled() { return locatorEnabled; }
    public boolean isBossBarEnabled() { return bossBarEnabled; }
    public String getBossBarText() { return bossBarText; }
    public int getBossBarUpdateInterval() { return bossBarUpdateInterval; }
    public String getEventStartMessage() { return eventStartMessage; }
    public String getEventEndMessage() { return eventEndMessage; }
    public String getPreAnnouncementMessage() { return preAnnouncementMessage; }
    public int getPreAnnouncementLeadMinutes() { return preAnnouncementLeadMinutes; }
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public int getParticleAmount() { return particleAmount; }
    public boolean isTitlesEnabled() { return titlesEnabled; }
    public String getStartTitle() { return startTitle; }
    public String getStartSubtitle() { return startSubtitle; }
    public String getEndTitle() { return endTitle; }
    public String getEndSubtitle() { return endSubtitle; }
    public boolean isSoundsEnabled() { return soundsEnabled; }
    public float getStartSoundVolume() { return startSoundVolume; }
    public float getStartSoundPitch() { return startSoundPitch; }
    public float getEndSoundVolume() { return endSoundVolume; }
    public float getEndSoundPitch() { return endSoundPitch; }

    // Getters for cached resources
    public Particle getCachedParticleType() { return cachedParticleType; }
    public Sound getCachedStartSound() { return cachedStartSound; }
    public Sound getCachedEndSound() { return cachedEndSound; }
    public BossBar.Color getCachedBossBarColor() { return cachedBossBarColor; }
    public BossBar.Overlay getCachedBossBarStyle() { return cachedBossBarStyle; }
}
