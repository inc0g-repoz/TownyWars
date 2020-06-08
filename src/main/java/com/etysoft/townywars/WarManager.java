package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Bukkit;

import javax.xml.bind.Marshaller;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarManager {

    private static Map<String, Town> neutralslist = new ConcurrentHashMap<>();
    private static Map<String, Town> townswarlist = new ConcurrentHashMap<>();
    private static Set<War> wars = new HashSet<War>();
   public static WarManager instance;

    public WarManager()
    {
        instance = this;
    }

    public War getTownWar(Town t)
    {
        for (War w : wars) {

            if (w.hasTown(t)) {
                return w;
            }
        }
        return null;
    }

    public void end(War w)
    {
        Town proig = w.getZeroPointTown();
        Town win = w.getNotZeroPointTown();
        if(proig != null)
        {
            List<TownBlock> tbs;
           tbs = proig.getTownBlocks();


               try {
                   for (TownBlock tb :
                           tbs) {
                       try {

                           tb.setTown(win);
                           tb.setOutpost(true);
                        TownyUniverse.getInstance().getDataSource().saveTownBlock(tb);



                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               }catch (Exception e){}

            TownyUniverse.getInstance().getDataSource().removeTown(proig);
               TownyUniverse.getInstance().getDataSource().saveAll();
        }
        else
        {
            Bukkit.getConsoleSender().sendMessage("Proig is null!");
        }
    }

    public boolean declare(Town a, Town j)
    {

        wars.add(new War(a, j, this));
        Bukkit.broadcastMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
        return  true;
    }

    public void addTownToWarList(Town t)
    {
        townswarlist.put(t.getName(), t);
    }

    public boolean isInWar(Town t)
    {
        return  townswarlist.containsKey(t.getName());
    }

    public boolean isNeutral(Town t)
    {
       return  neutralslist.containsKey(t.getName());
    }

    public boolean isNeutral(String name)
    {
        return  neutralslist.containsKey(name);
    }
}
