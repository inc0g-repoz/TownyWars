package com.etysoft.townywars.events;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarDeclareEvent extends Event  implements Cancellable {

    private Town attaker;
    private Town victim;
    private boolean canceled = false;

    public WarDeclareEvent(Town attaker, Town victim)
    {
        this.attaker = attaker;
        this.victim = victim;
    }

    public Town getAttaker()
    {
        return attaker;
    }

    public Town getVictim()
    {
        return victim;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
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
