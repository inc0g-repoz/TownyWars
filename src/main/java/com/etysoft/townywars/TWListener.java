package com.etysoft.townywars;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TWListener implements Listener {

    @EventHandler
    public void onjoin(PlayerJoinEvent e)
    {
        //e.getPlayer().sendMessage("пошёл нахуй!");
    }

    @EventHandler
    public void kill(PlayerDeathEvent e)
    {
        Player plr = e.getEntity();

        EntityDamageEvent edc = e.getEntity().getLastDamageCause();
        if (!(edc instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) edc;
        if (!(edbee.getDamager() instanceof Player)) {
            return;
        }
        Player attacker = (Player) edbee.getDamager();
        Player victim = (Player) edbee.getEntity();
        try {
            Resident r1 = TownyUniverse.getDataSource().getResident(attacker.getName());
            Resident r2 = TownyUniverse.getDataSource().getResident(victim.getName());

            boolean isr1 = WarManager.instance.isInWar(r1.getTown());

           War war = WarManager.instance.getTownWar(r1.getTown());
           if(war != null)
           {
               if(war.hasTown(r2.getTown()))
               {
                   Town tv = war.minus(r2.getTown());
                   if(tv != null)
                   {
                       Bukkit.broadcastMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-end").replace("%s", tv.getName()).replace("%j", r2.getTown().getName())));
                       WarManager.instance.end(war);

                   }
                   else
                   {
                       victim.sendMessage("Now " + war.getAPoints() + " : " + war.getJPoints());
                       attacker.sendMessage("Now " + war.getAPoints() + " : " + war.getJPoints());
                   }

               }
           }
           else
           {
               //no war
           }

        } catch (NotRegisteredException ex) {
            ex.printStackTrace();
        }

    }
}
