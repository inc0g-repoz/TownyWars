package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class Listeners implements org.bukkit.event.Listener {

    @EventHandler
    public void onjoin(PlayerJoinEvent e) {
        if(e.getPlayer().hasPermission("twar.admin")) {
            if (TownyWars.instance.isPreRelease) {
                e.getPlayer().sendMessage("PreRelease of TownyWars " + TownyWars.instance.getDescription().getVersion() + "! Thanks for testing!");
            }

            if (!TownyWars.latestVersion.equals(TownyWars.instance.getDescription().getVersion())) {
                e.getPlayer().sendMessage("New version of TownyWars is available to download (" + TownyWars.latestVersion + ")");
            }
        }
    }

    @EventHandler
    public void onResidentLeave(TownRemoveResidentEvent event) {
      if(WarManager.getInstance().isInWar(event.getTown())) {
          War w = WarManager.getInstance().getTownWar(event.getTown());
          if (event.getTown() == w.getVictim()) {
              w.minusV();
          } else {
              w.minusA();
          }
      }
    }

    @EventHandler
    public void nt(NewTownEvent n) {
        if(TownyWars.instance.getConfig().getBoolean("create-neutral")) {
            WarManager.getInstance().setNeutrality(true, n.getTown());
        }
    }

    @EventHandler
    public void BlockDispense(BlockDispenseEvent e) {
        if (e.getItem().getType().getId() == 259) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Delete(PreDeleteTownEvent n) {
       if(WarManager.getInstance().isInWar(n.getTown()) && TownyWars.instance.getConfig().getBoolean("block-town-delete")) {
           n.setCancelled(true);
           n.setCancelMessage("Town in war!");
       } else {
           if (WarManager.getInstance().isNeutral(n.getTown())) {
               WarManager.getInstance().setNeutrality(false, n.getTown());
           }

           if (TownyWars.instance.getConfig().getBoolean("trfeatures")) {
               TownyMessaging.sendGlobalMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("bye-bye")).replace("%s", n.getTown().getName())));
           }
       }
    }

    @EventHandler
    public void onResidentAdd(TownAddResidentEvent event) {
        if(WarManager.getInstance().isInWar(event.getTown())) {
            War w = WarManager.getInstance().getTownWar(event.getTown());
            if (event.getTown() == w.getVictim()) {
                w.minusA();
            } else {
                w.minusV();
            }
        }
    }

    @EventHandler
    public void kill(PlayerDeathEvent e) {
        EntityDamageEvent edc = e.getEntity().getLastDamageCause();
        if (!(edc instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) edc;
        Player attacker;
        if(edbee.getDamager() instanceof  Arrow) {
            Arrow attacker2 = (Arrow) edbee.getDamager();
            ProjectileSource attacker23 =  attacker2.getShooter();
            if(attacker23 instanceof Player) {
                attacker = (Player) attacker23;
            } else {
                return;
            }
        } else if(edbee.getDamager() instanceof Player) {
            attacker = (Player) edbee.getDamager();
        } else {
            return;
        }

        Player victim = (Player) edbee.getEntity();
        try {
            Resident r1 =   com.palmergames.bukkit.towny.TownyUniverse.getInstance().getDataSource().getResident(attacker.getName());
            Resident r2 =   com.palmergames.bukkit.towny.TownyUniverse.getInstance().getDataSource().getResident(victim.getName());
            War war = WarManager.getInstance().getTownWar(r1.getTown());
            if(war != null) {
                if(war.hasTown(r2.getTown())) {
                    if(war.isASide(r1.getTown()) != war.isASide(r2.getTown())) {
                        Town tv = war.minus(r2.getTown());
                        if (tv != null) {
                            int nmessage = TownyWars.instance.getConfig().getInt("public-announce-warend");
                            if (nmessage == 2) {
                                Bukkit.broadcastMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-end")).replace("%s", tv.getName()).replace("%j", r2.getTown().getName())));
                            } else {
                                TownyMessaging.sendTownMessagePrefixed(r1.getTown(), ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-end")).replace("%s", tv.getName()).replace("%j", r2.getTown().getName())));
                                TownyMessaging.sendTownMessagePrefixed(r2.getTown(), ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-end")).replace("%s", tv.getName()).replace("%j", r2.getTown().getName())));
                            }

                            WarManager.getInstance().end(war, true);
                        } else {
                            victim.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-points")).replace("%s", war.getAPoints() + "").replace("%k", war.getVPoints() + "")));
                            attacker.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-points")).replace("%s", war.getAPoints() + "").replace("%k", war.getVPoints() + "")));
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
    }
}
