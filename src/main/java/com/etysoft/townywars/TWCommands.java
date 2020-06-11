package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.plugin2.main.server.Plugin;

import java.util.Set;
import java.util.function.Consumer;

public class TWCommands implements CommandExecutor {

   public static TownyWars instance;

    public TWCommands(TownyWars TownyWars)
    {
        instance = TownyWars;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("twar"))
        {
            if(args.length > 0)
            {
                if(args[0].equals("info"))
                {
                    if(sender.hasPermission("twar.use")) {
                        Info.plugin(sender, instance, instance.towny);
                    }
                    else
                    {
                        sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                    }
                }
                else if(args[0].equals("reload"))
                {
                    if(sender.hasPermission("twar.admin"))
                    {
                        instance.reloadConfig();
                        instance.ConfigInit();
                        sender.sendMessage(fun.cstring("&aSuccessfully reloaded configs!"));
                    }
                    else
                    {
                        sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                    }
                }
                else if(args[0].equals("declare"))
                {
                    if(sender.hasPermission("twar.mayor"))
                    {
                       if(sender instanceof Player)
                       {
                           Player p = (Player) sender;

                           try {
                               Resident r =  TownyUniverse.getDataSource().getResident(p.getName());
                               if(r.hasTown())
                               {
                                    if(args.length > 1)
                                    {
                                        if(r.getTown().getHoldingBalance() >= TownyWars.instance.getConfig().getDouble("price-declare"))
                                        {




                                        if(TownyUniverse.getDataSource().hasTown(args[1])) {
                                            Town tod = TownyUniverse.getDataSource().getTown(args[1]);
                                            if (tod != null) {
                                                if (!WarManager.instance.isNeutral(tod) && !WarManager.instance.isNeutral(r.getTown())) {
                                                    if(!WarManager.getInstance().isInWar(r.getTown())) {
                                                        boolean success = WarManager.instance.declare(r.getTown(), tod);

                                                        if (!success) {
                                                            p.sendMessage(fun.cstring(instance.getConfig().getString("msg-wrtown")));
                                                        } else {
                                                            r.getTown().pay(TownyWars.instance.getConfig().getDouble("price-declare"), "War declare");
                                                        }
                                                    }
                                                } else {
                                                    p.sendMessage(fun.cstring(instance.getConfig().getString("msg-ntown")));
                                                }
                                            } else {
                                                p.sendMessage(fun.cstring(instance.getConfig().getString("msg-wrtown")));
                                            }
                                        }
                                        else
                                        {
                                            sender.sendMessage(fun.cstring(instance.getConfig().getString("msg-tde")));
                                        }
                                        }
                                        else
                                        {
                                            p.sendMessage(fun.cstring(instance.getConfig().getString("msg-money").replace("%s", TownyWars.instance.getConfig().getDouble("price-declare") + "")));

                                        }
                                    }
                                    else
                                    {
                                        sender.sendMessage(fun.cstring(instance.getConfig().getString("no-args")));
                                    }

                               }
                               else
                               {
                                      p.sendMessage(fun.cstring(instance.getConfig().getString("msg-notown")));
                               }
                           } catch (NotRegisteredException | EconomyException e) {
                               e.printStackTrace();
                           }
                       }
                       else
                       {
                           sender.sendMessage("You can't do it from Console!");
                       }
                    }
                    else
                    {
                        sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                    }
                }
                else if(args[0].equals("st"))
                {
                    if(WarManager.instance.getWars().size() > 0) {
                        sender.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-war")));
                        for (War w :
                                WarManager.instance.getWars()) {

                            sender.sendMessage(fun.cstring("&e" + w.getAttacker().getName() + "&f(&b" + w.getAPoints() + ") VS " + "&e" + w.getJertva().getName() + "&f(&b" + w.getJPoints() + ")"));
                        }
                    }
                    else
                    {
                        sender.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-warde")));
                    }
                }
                else if(args[0].equals("fend"))
                {
                   if(sender.hasPermission("twar.admin"))
                   {
                       if(args.length > 1)
                       {
                           try {
                               Town t = TownyUniverse.getDataSource().getTown(args[1]);
                               War w = WarManager.getInstance().getTownWar(t);
                               WarManager.getInstance().end(w, false);
                               sender.sendMessage("War " + w.getJertva() + " VS " + w.getAttacker() + " stopped without pain ;)");
                           } catch (NotRegisteredException e) {
                               sender.sendMessage("Not registered!");
                           }

                       }
                   }
                   else
                   {
                       sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                   }
                }
                else if(args[0].equals("help"))
                {
                    if(sender.hasPermission("twar.use"))
                    {
                        Info.help(sender, TownyWars.instance);
                    }
                    else
                    {
                        sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                    }
                }
                else if(args[0].equals("n"))
                {
                    if(sender instanceof Player)
                    {
                        if(sender.hasPermission("twar.mayor")) {
                            try {
                                Resident r = TownyUniverse.getDataSource().getResident(sender.getName());
                                if (r.hasTown()) {
                                    if(!WarManager.getInstance().isInWar(r.getTown())) {
                                        if (r.getTown().getHoldingBalance() >= TownyWars.instance.getConfig().getDouble("price-neutral")) {
                                            int nmessage = TownyWars.instance.getConfig().getInt("public-announce-neutral");
                                            if (WarManager.getInstance().isNeutral(r.getTown())) {
                                                r.getTown().pay(TownyWars.instance.getConfig().getDouble("price-neutral"), "Neutrality toggle");
                                                WarManager.getInstance().setNeutrality(false, r.getTown());

                                                if (nmessage == 3) {
                                                    sender.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-noff").replace("%s", r.getTown().getName())));
                                                } else if (nmessage == 1) {
                                                    TownyMessaging.sendTownMessagePrefixed(r.getTown(), fun.cstring(TownyWars.instance.getConfig().getString("msg-noff").replace("%s", r.getTown().getName())));
                                                } else if (nmessage == 2) {
                                                    TownyMessaging.sendGlobalMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-noff").replace("%s", r.getTown().getName())));
                                                }

                                            } else {
                                                WarManager.getInstance().setNeutrality(true, r.getTown());
                                                if (nmessage == 3) {
                                                    sender.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-non").replace("%s", r.getTown().getName())));
                                                } else if (nmessage == 2) {
                                                    TownyMessaging.sendTownMessagePrefixed(r.getTown(), fun.cstring(TownyWars.instance.getConfig().getString("msg-non").replace("%s", r.getTown().getName())));
                                                } else if (nmessage == 1) {
                                                    TownyMessaging.sendGlobalMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-non").replace("%s", r.getTown().getName())));
                                                }

                                            }
                                        }
                                    }
                                    else
                                    {
                                        sender.sendMessage("You must be not in war!");
                                    }
                                }
                                else
                                {
                                    sender.sendMessage(fun.cstring(instance.getConfig().getString("msg-money").replace("%s", TownyWars.instance.getConfig().getDouble("price-neutral") + "")));
                                }
                            } catch (Exception e) {
                                Bukkit.getConsoleSender().sendMessage("TOWNYWARS CATCH AN ERROR:");
                                e.printStackTrace();
                                Bukkit.getConsoleSender().sendMessage("ERROR IN NEUTRALITY TOGGLE");
                            }
                        }
                        else
                        {
                            sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                        }
                    }
                   else
                    {
                        sender.sendMessage("You can't do it from Console!");
                    }

                }
                else if(args[0].equals("nlist"))
                {
                    if(sender.hasPermission("twar.use")) {




                        String list = "";
                        Set<Town> ts = WarManager.instance.getNTowns();
                        if(ts.size() > 0) {
                            Boolean isfirst = true;
                            for (Town t :
                                    ts) {
                                if (isfirst) {
                                    list = "&e" + t.getName();
                                    isfirst = false;
                                } else {
                                    list = list + "&f, &e" + t.getName();
                                }


                            }
                            sender.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-nlist")));
                            sender.sendMessage(fun.cstring(list));
                        }
                        else
                        {
                            sender.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-listde")));
                        }
                    }
                    else
                    {
                        sender.sendMessage(fun.cstring(instance.getConfig().getString("no-perm")));
                    }
                }
                else
                {
                    sender.sendMessage(fun.cstring(instance.getConfig().getString("no-args")));
                }
            }
            else
            {
                sender.sendMessage(fun.cstring(instance.getConfig().getString("no-args")));
            }

            return true;
        }
        return false;
    }
}
