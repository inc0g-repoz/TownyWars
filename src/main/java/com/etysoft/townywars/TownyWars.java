package com.etysoft.townywars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWars extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("Хеллоу ебать!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
