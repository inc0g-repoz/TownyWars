package com.etysoft.townywars;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class Listeners implements org.bukkit.event.Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
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
    public void onQuit(PlayerQuitEvent e) {
        e.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
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
    public void onNewTown(NewTownEvent n) {
        if(TownyWars.instance.getConfig().getBoolean("create-neutral")) {
            WarManager.getInstance().setNeutrality(true, n.getTown());
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        if (e.getItem().getType().getId() == 259) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreDeleteTown(PreDeleteTownEvent n) {
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
    public void onDeath(PlayerDeathEvent e) {
        EntityDamageEvent edc = e.getEntity().getLastDamageCause();
        if (!(edc instanceof EntityDamageByEntityEvent)) {
            return;
        }

        EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) edc;
        Player attacker;
        if (edbee.getDamager() instanceof  Arrow) {
            Arrow arrow = (Arrow) edbee.getDamager();
            ProjectileSource shooter =  arrow.getShooter();
            if (shooter instanceof Player) {
                attacker = (Player) shooter;
            } else {
                return;
            }
        }
        else {
            if (edbee.getDamager() instanceof Player) {
                attacker = (Player) edbee.getDamager();
            }
            else {
                return;
            }
        }

        Player deadGuy = (Player) edbee.getEntity();
        try {
            Resident killer = com.palmergames.bukkit.towny.TownyUniverse.getInstance().getDataSource().getResident(attacker.getName());
            Resident victim = com.palmergames.bukkit.towny.TownyUniverse.getInstance().getDataSource().getResident(deadGuy.getName());
            War war = WarManager.getInstance().getTownWar(killer.getTown());
            if (war != null) {
                if (war.hasTown(victim.getTown())) {
                    if (war.isASide(killer.getTown()) != war.isASide(victim.getTown())) {
                        Town tv = war.minus(victim.getTown());
                        if (tv != null) {
                            int nmessage = TownyWars.instance.getConfig().getInt("public-announce-warend");
                            if (nmessage == 2) {
                                Bukkit.broadcastMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-end"))
                                        .replace("%s", tv.getName())
                                        .replace("%j", victim.getTown().getName())));
                            } else {
                                TownyMessaging.sendTownMessagePrefixed(killer.getTown(), ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-end"))
                                        .replace("%s", tv.getName())
                                        .replace("%j", victim.getTown().getName())));
                                TownyMessaging.sendTownMessagePrefixed(victim.getTown(), ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-end"))
                                        .replace("%s", tv.getName())
                                        .replace("%j", victim.getTown().getName())));
                            }

                            WarManager.getInstance().end(war, true);
                        } else {
                            deadGuy.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-points"))
                                    .replace("%s", war.getAPoints() + "")
                                    .replace("%k", war.getVPoints() + "")));
                            attacker.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-points"))
                                    .replace("%s", war.getAPoints() + "")
                                    .replace("%k", war.getVPoints() + "")));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
