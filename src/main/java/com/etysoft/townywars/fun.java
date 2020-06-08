package com.etysoft.townywars;

import org.bukkit.ChatColor;

public class fun
{
    public static String cstring(String text)
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
