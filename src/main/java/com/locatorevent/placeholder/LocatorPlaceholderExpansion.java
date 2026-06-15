package com.locatorevent.placeholder;

import com.locatorevent.LocatorEvent;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class LocatorPlaceholderExpansion extends PlaceholderExpansion {

    private final LocatorEvent plugin;

    public LocatorPlaceholderExpansion(LocatorEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "locatorevent";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Jules";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("state")) {
            return plugin.getEventManager().getState().name();
        }

        if (params.equalsIgnoreCase("time_left")) {
            if (plugin.getEventManager().getState() == com.locatorevent.manager.EventManager.EventState.INACTIVE) {
                return "00:00";
            }
            return plugin.getEventManager().getFormattedTimeLeft();
        }

        return null;
    }
}
