package com.locatorevent;

import com.locatorevent.manager.BossBarManager;
import com.locatorevent.manager.ConfigManager;
import com.locatorevent.manager.EventManager;
import com.locatorevent.placeholder.LocatorPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class LocatorEvent extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private EventManager eventManager;
    private BossBarManager bossBarManager;

    @Override
    public void onEnable() {
        // Initialize Managers
        this.configManager = new ConfigManager(this);
        this.eventManager = new EventManager(this);
        this.bossBarManager = new BossBarManager(this);

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(this, this);

        // Register Placeholders
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LocatorPlaceholderExpansion(this).register();
        }

        // Start Scheduler
        eventManager.startScheduler();

        getLogger().info("LocatorEvent has been enabled!");
    }

    @Override
    public void onDisable() {
        if (eventManager != null && eventManager.getState() == EventManager.EventState.ACTIVE) {
            eventManager.endEvent(false);
        }
        getLogger().info("LocatorEvent has been disabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("locatorevent")) {
            if (!sender.hasPermission("locatorevent.admin")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    configManager.reload();
                    sender.sendMessage("§aLocatorEvent configuration reloaded!");
                    return true;
                } else if (args[0].equalsIgnoreCase("start")) {
                    eventManager.startEvent();
                    sender.sendMessage("§aLocatorEvent started manually!");
                    return true;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    eventManager.endEvent();
                    sender.sendMessage("§cLocatorEvent stopped manually!");
                    return true;
                }
            }

            sender.sendMessage("§6LocatorEvent Commands:");
            sender.sendMessage("§e/locatorevent reload §7- Reloads the config");
            sender.sendMessage("§e/locatorevent start §7- Force starts the event");
            sender.sendMessage("§e/locatorevent stop §7- Force stops the event");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        bossBarManager.addPlayer(event.getPlayer());
        // No longer need to manually update maps here as GameRule is per-world.
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        bossBarManager.removePlayer(event.getPlayer());
    }

    public ConfigManager getConfigManager() { return configManager; }
    public EventManager getEventManager() { return eventManager; }
    public BossBarManager getBossBarManager() { return bossBarManager; }
}
