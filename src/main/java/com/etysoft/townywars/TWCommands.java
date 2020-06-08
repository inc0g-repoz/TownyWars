package com.etysoft.townywars;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.plugin2.main.server.Plugin;

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
                               Resident r = TownyUniverse.getDataSource().getResident(p.getName());
                               if(r.hasTown())
                               {
                                    if(args.length > 1)
                                    {
                                        Town tod = TownyUniverse.getDataSource().getTown(args[1]);
                                        if(tod != null)
                                        {
                                            if(!WarManager.instance.isNeutral(tod) && !WarManager.instance.isNeutral(r.getTown()))
                                            {
                                                WarManager.instance.declare(r.getTown(), tod);

                                            }
                                            else
                                            {
                                                p.sendMessage(fun.cstring(instance.getConfig().getString("msg-ntown")));
                                            }
                                        }
                                        else
                                        {
                                            p.sendMessage(fun.cstring(instance.getConfig().getString("msg-wrtown")));
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
                           } catch (NotRegisteredException e) {
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
