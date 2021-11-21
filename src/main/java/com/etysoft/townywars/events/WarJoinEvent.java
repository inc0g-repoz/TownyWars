package com.etysoft.townywars.events;

import com.etysoft.townywars.War;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarJoinEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private Town newTown;
    private War war;
    private boolean isASide;
    private boolean canceled = false;

    public WarJoinEvent(War war, Town newTown, boolean isASide) {
        this.war = war;
        this.newTown = newTown;
        this.isASide = isASide;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public War getWar() {
        return war;
    }

    public boolean isASide() {
        return isASide;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }

	public Town getNewTown() {
		return newTown;
	}
}

