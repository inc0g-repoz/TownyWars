package com.etysoft.townywars.events;

import com.etysoft.townywars.War;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarEndEvent extends Event implements Cancellable {
    private War war;
    private boolean canceled = false;
    private static final HandlerList HANDLERS = new HandlerList();

    public WarEndEvent(War war) {
        this.war = war;
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

	public War getWar() {
		return war;
	}
}
