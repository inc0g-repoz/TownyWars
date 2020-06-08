package com.etysoft.townywars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWars extends JavaPlugin {


    public Plugin towny;
    public static TownyWars instance;
    public WarManager wm;
    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(new TWListener(), this);
        instance = this;
        getCommand("townwars").setExecutor(new TWCommands(this));
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
        }
        Bukkit.getConsoleSender().sendMessage("Initializing config.yml...");
        ConfigInit();
           wm = new WarManager();
        Bukkit.getConsoleSender().sendMessage("TownWars " + this.getDescription().getVersion() + " successfully enabled!");
    }
    private String supported = "0.96.1.0";
    public boolean isCompatible(String version)
    {
        return supported.contains(version);
    }



    public void ConfigInit()
    {
        getConfig().addDefault("msg-compatible", "&aCompatible with current Towny version");
        getConfig().addDefault("msg-nocompatible", "&cWasn't tested with Towny %s");
        getConfig().addDefault("no-args", "&cWrong arguments.");
        getConfig().addDefault("no-perm", "&cYou do not have permission.");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("TownWars " + this.getDescription().getVersion() + " successfully disabled!");
    }
}
