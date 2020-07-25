package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.object.EconomyAccount;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class TownyWars extends JavaPlugin {


    public Plugin towny;
    public static TownyWars instance;
    public WarManager wm;
    public boolean isPreRelease = true;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if(isPreRelease)
        {
            Bukkit.getConsoleSender().sendMessage("You are using the pre-release of TownyWars! If any errors occur, please contact Discord using the link https://discord.gg/Etd4XXH");
        }

        if(getServer().getPluginManager().getPlugin("Towny") == null)
        {
            Bukkit.getConsoleSender().sendMessage("Towny doesn't found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else
        {
            towny =  getServer().getPluginManager().getPlugin("Towny");

            Bukkit.getConsoleSender().sendMessage("Using Towny " +  towny.getDescription().getVersion());
            if(isCompatible(towny.getDescription().getVersion()))
            {
                Bukkit.getConsoleSender().sendMessage("Towny version was tested with plugin.");
            }
            else
            {
                Bukkit.getConsoleSender().sendMessage("Towny version wasn't tested with TownyWars!");
            }

            // Deprecated, will be removed in future updates
            try {
                Bukkit.getConsoleSender().sendMessage("Initializing Towny plugin before WarManager started for correct data...");
                getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("Towny"));

                Bukkit.getConsoleSender().sendMessage("TownyWars was successfully enabled Towny");
            }
          catch (Exception e)
          {
              Bukkit.getConsoleSender().sendMessage("TownyWars can't enable Towny(is it already enabled?)");
          }


        }
        isGood();
        if(getConfig().getDouble("config-ver") != 1.2)
        {
            Bukkit.getConsoleSender().sendMessage("Outdated configuration file!");
        }
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        instance = this;
        getCommand("townwars").setExecutor(new Commands(this));
        getCommand("townwars").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
                List<String> tabs = new ArrayList<>();
                if(args.length == 1)
                {
                    tabs.add("declare");
                    tabs.add("n");
                    tabs.add("st");
                    tabs.add("nlist");
                    tabs.add("joinwar");
                    tabs.add("end");
                    if(sender.hasPermission("twar.admin"))
                    {
                        tabs.add("reload");
                        tabs.add("fend");
                    }
                    tabs.add("info");
                    tabs.add("help");
                    tabs.add("canceljw");
                    tabs.add("invite");
                }
                else
                {
                    if(args[0].equals("declare") || args[0].equals("fend") || args[0].equals("invite"))
                    {
                        List<Town> towns = TownyUniverse.getInstance().getDataSource().getTowns();
                        for(Town t : WarManager.getInstance().getNeutralTowns())
                        {
                            towns.remove(t);
                        }
                        for(Town t : towns)
                        {
                            tabs.add(t.getName());
                        }
                    }
                    else if(args[0].equals("n") || args[0].equals("st") || args[0].equals("joinwar"))
                    {
                        List<Town> towns = TownyUniverse.getInstance().getDataSource().getTowns();

                        for(Town t : towns)
                        {
                            tabs.add(t.getName());
                        }
                    }
                }
                List<String> finals = new ArrayList<>();

                for(String s : tabs)
                {
                    if(args.length == 1) {
                        if (s.contains(args[0])) {
                            finals.add(s);
                        }
                    }
                    else if(args.length == 2)
                    {
                        if (s.contains(args[1])) {

                            finals.add(s);
                        }
                    }
                }

                return finals;
            }
        });
        Bukkit.getConsoleSender().sendMessage("Initializing bStats metrics...");
        try {
            int pluginId = 7801; // <-- Replace with the id of your plugin!
            Metrics metrics = new Metrics(this, pluginId);
        }
        catch (Exception e)
        {
            Bukkit.getConsoleSender().sendMessage("Can't initialize metrics!");
        }
        Bukkit.getConsoleSender().sendMessage("Initializing config.yml...");
        ConfigInit();
           wm = new WarManager();
        Bukkit.getConsoleSender().sendMessage("TownWars " + this.getDescription().getVersion() + " successfully enabled!");
    }
    private String supported = "0.96.1.0, 0.96.2.0";
    public boolean isCompatible(String version)
    {
        return supported.contains(version);
    }



    public void ConfigInit()
    {
        getConfig().addDefault("msg-compatible", "&aCompatible with current Towny version");
        getConfig().addDefault("msg-nocompatible", "&cWasn't tested with Towny %s");
        getConfig().addDefault("msg-declare", "&b%s &cdeclared war on &6%j!");
        getConfig().addDefault("msg-end", "&b%s &cwin war on &6%j!");
        getConfig().addDefault("msg-notown", "&cYou don't belong to a town!");
        getConfig().addDefault("msg-wrtown", "&cWrong town!");
        getConfig().addDefault("msg-ntown", "&cNeutral town can't be in war!");
        getConfig().addDefault("msg-wrtown", "&cWrong town!");
        getConfig().addDefault("msg-war", "&bCurrent wars: ");
        getConfig().addDefault("msg-nlist", "&aNeutral towns: ");
        getConfig().addDefault("msg-non", "&cNow your town no longer neutral");
        getConfig().addDefault("msg-noff", "&aNow your town is neutral");
        getConfig().addDefault("msg-listde", "&cNo neutral towns!");
        getConfig().addDefault("msg-warde", "&cNo active wars!");
        getConfig().addDefault("msg-tde", "&aTown doesn't exists!");

        getConfig().addDefault("public-announce-neutral", 2);
        getConfig().addDefault("public-announce-warstart", 2);
        getConfig().addDefault("public-announce-warend", 2);

        getConfig().addDefault("no-args", "&cWrong arguments.");
        getConfig().addDefault("no-args", "&cWrong arguments.");

        getConfig().addDefault("msg-win", "&aWinner! &bYour town win the war and now you have all territory of loser town!");
        getConfig().addDefault("msg-lose", "&cLose! &bYour town lose the war and now all your territory has another town!");

        getConfig().addDefault("trfeatures", true);
        getConfig().addDefault("only-town-delete", false);
        getConfig().addDefault("bye-bye", "&6Пока-пока, &b%s.&6 F.");
        saveDefaultConfig();
        instance.reloadConfig();
    }


    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DataManager.saveNeutrals(WarManager.getInstance().getNeutralTowns());
        Bukkit.getConsoleSender().sendMessage("TownWars " + this.getDescription().getVersion() + " successfully disabled!");
    }

    public boolean isGood()
    {
        try
        {
            TownyUniverse.getInstance().getDataSource();
            return true;
        }
        catch (Exception e)
        {
            Bukkit.getConsoleSender().sendMessage("You are using an incompatible version of TownyWars with your version of Towny! (DataSource, old Towny version)");
            return false;
        }
    }
}
