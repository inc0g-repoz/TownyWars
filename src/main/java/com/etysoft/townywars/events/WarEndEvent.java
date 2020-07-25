package com.etysoft.townywars.events;

import com.etysoft.townywars.War;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarEndEvent extends Event implements Cancellable {

        private War war;
        private boolean canceled = false;

        public WarEndEvent(War war)
        {
            this.war = war;
        }

        public War getWar()
        {
            return war;
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
