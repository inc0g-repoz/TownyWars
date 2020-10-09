package com.etysoft.townywars;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class Info {
    public static void plugin(CommandSender p, TownyWars pl, Plugin towny) {
        p.sendMessage(ChatColor.WHITE + "============ " + ChatColor.AQUA + "TownyWars" + ChatColor.WHITE + " ============");
        p.sendMessage(ColorCodes.toColor("Version: " + pl.getDescription().getVersion()));
        p.sendMessage(ColorCodes.toColor("Author: &bkarlov_m"));
        p.sendMessage(ColorCodes.toColor("GitHub: &bhttps://github.com/karlovm/TownyWars"));
        if(pl.isCompatible(towny.getDescription().getVersion())) {
            p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("msg-compatible")));
        } else {
            p.sendMessage(ColorCodes.toColor(Objects.requireNonNull(pl.getConfig().getString("msg-nocompatible")).replace("%s", towny.getDescription().getVersion())));
        }

        p.sendMessage("==============================");
    }

    public static void help(CommandSender p, TownyWars pl) {
        p.sendMessage(ChatColor.WHITE + "============ " + ChatColor.AQUA + ColorCodes.toColor(pl.getConfig().getString("ht")) + ChatColor.WHITE + " ============");
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht1")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht2")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht3")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht4")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht5")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht6")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht7")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht8")));
        p.sendMessage(ColorCodes.toColor(pl.getConfig().getString("ht9")));
    }
}
