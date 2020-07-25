package com.etysoft.townywars;

import org.bukkit.ChatColor;

public class ColorCodes
{
    public static String toColor(String text)
    {
        try
        {
            return  ChatColor.translateAlternateColorCodes('&', text);
        }
        catch (Exception e)
        {
            return text;
        }
    }
}
