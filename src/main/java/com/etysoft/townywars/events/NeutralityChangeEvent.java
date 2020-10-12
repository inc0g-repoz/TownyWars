package com.etysoft.townywars.events;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NeutralityChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isNeutral;
    private Town town;
    private boolean canceled = false;

    public NeutralityChangeEvent(Town town, boolean isNeutral) {
        this.town = town;
        this.isNeutral = isNeutral;
    }

    public Town getTown() {
        return town;
    }

    public boolean isNeutral() {
        return isNeutral;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }
}
