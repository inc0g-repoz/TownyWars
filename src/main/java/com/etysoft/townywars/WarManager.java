package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarManager {

    private static  Set<Town> neutralslist = new HashSet<Town>();
    private static Map<String, Town> townswarlist = new ConcurrentHashMap<>();
    private static Map<Town, Town> addreq = new ConcurrentHashMap<>();
    private static Set<War> wars = new HashSet<War>();
   public static WarManager instance;

    public WarManager()
    {
        neutralslist = DataManager.loadNeutrals();
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

    public boolean isSended(Town from)
    {
       return  addreq.containsKey(from);
    }

    public boolean hasRequest(Town from, Town to)
    {
        if(addreq.containsKey(from))
        {
            return addreq.get(from) == to;
        }
        else
        {
            return false;
        }
    }

    public void removeRequest(Town from)
    {
        addreq.remove(from);
    }

    public boolean sendRequest(Town from, Town to, Player p2)
    {

        if(!WarManager.getInstance().isInWar(from) && WarManager.getInstance().isInWar(to)) {

            if(!WarManager.getInstance().isNeutral(from) && WarManager.getInstance().getTownWar(to).isSide(to)) {
                Player p = Bukkit.getPlayer(to.getMayor().getName());

                if (p != null) {
                    p.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-req").replace("%s", from.getName())));
                    addreq.put(from, to);
                    p2.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-sendreq")));
                    return true;
                } else {
                    p2.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-mayoroffline")));
                    return false;
                }


            }
            else
            {
                p2.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-ntown")));
                return false;
            }
        }
        else
        {
            p2.sendMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-ninwar")));
            return false;
        }
    }

    public Set<Town> getNTowns()
    {
        return neutralslist;
    }

    public Set<War> getWars()
    {
        return wars;
    }

    public static WarManager getInstance()
    {
        return  instance;
    }



    public void end(War w, boolean withpain)
    {
        if(!withpain)
        {
            wars.remove(w);
            if(townswarlist.containsKey(w.getJertva().getName()))
            {
                townswarlist.remove(w.getJertva().getName());
            }
            if(townswarlist.containsKey(w.getAttacker().getName()))
            {
                townswarlist.remove(w.getAttacker().getName());
            }
            for (Town t : w.getATowns())
            {
                t.setAdminEnabledPVP(false);
                townswarlist.remove(t.getName());
            }
            for (Town t : w.getJTowns())
            {
                t.setAdminEnabledPVP(false);
                townswarlist.remove(t.getName());
            }
            w.getAttacker().setAdminEnabledPVP(false);
            w.getJertva().setAdminEnabledPVP(false);
             w.clear();
        }
        else {
            Town proig = w.getZeroPointTown();
            Town win = w.getNotZeroPointTown();

            if (proig != null) {


                if (!TownyWars.instance.getConfig().getBoolean("only-town-delete")) {
                    List<TownBlock> tbs = null;
                    try
                    {
                       tbs = new ArrayList<>(proig.getTownBlocks());
                    }
                    catch (Exception e)
                    {
                        Bukkit.getConsoleSender().sendMessage("Error! Please report this to Discord https://discord.gg/Etd4XXH");
                    }

                    List<TownBlock> tList = new ArrayList<TownBlock>();
                    tList.addAll(tbs);


                    for (TownBlock tb :
                            tList) {
                        try {

                            tb.setTown(win);

                            TownyUniverse.getInstance().getDataSource().saveTownBlock(tb);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                win.setAdminEnabledPVP(false);

                TownyMessaging.sendTownMessage(proig, fun.cstring(TownyWars.instance.getConfig().getString("msg-lose")));
                TownyMessaging.sendTownMessage(win, fun.cstring(TownyWars.instance.getConfig().getString("msg-win")));
              w.clear();
                wars.remove(w);
                for (Town t : w.getATowns())
                {
                    t.setAdminEnabledPVP(false);
                    townswarlist.remove(t.getName());
                }
                for (Town t : w.getJTowns())
                {
                    t.setAdminEnabledPVP(false);
                    townswarlist.remove(t.getName());
                }
                if (townswarlist.containsKey(w.getJertva().getName())) {
                    townswarlist.remove(w.getJertva().getName());
                }
                if (townswarlist.containsKey(w.getAttacker().getName())) {
                    townswarlist.remove(w.getAttacker().getName());
                }
                TownyUniverse.getInstance().getDataSource().deleteTown(proig);
                TownyUniverse.getInstance().getDataSource().removeTown(proig);

            } else {
                Bukkit.getConsoleSender().sendMessage("Proig is null!");
            }
        }
    }

    public void setNeutrality(Boolean neutrality, Town t)
    {
      if(neutrality)
      {
          if(!neutralslist.contains(t))
          {
              neutralslist.add(t);
          }

      }
      else
      {
          if(neutralslist.contains(t))
          {
              neutralslist.remove(t);
          }
      }
    }

    public boolean addTownToWar(Town t, War w, boolean isAside)
    {
        if(!isInWar(t))
        {
            t.setAdminEnabledPVP(true);
            if(isAside)
            {
                w.addATown(t);
            }
            else
            {
                w.addJTown(t);
            }
            addTownToWarList(t);

            return true;
        }
        else
        {
            return false;
        }
    }


    public boolean declare(Town a, Town j)
    {
       if(a != j) {
           if(!WarManager.getInstance().isInWar(a) && !WarManager.getInstance().isInWar(j)) {
               a.setAdminEnabledPVP(true);
               j.setAdminEnabledPVP(true);
               wars.add(new War(a, j, this));

               if (TownyWars.instance.getConfig().getInt("public-announce-warstart") == 2) {
                   Bukkit.broadcastMessage(fun.cstring(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
               } else if (TownyWars.instance.getConfig().getInt("public-announce-warstart") == 1) {
                   TownyMessaging.sendTownMessagePrefixed(j, fun.cstring(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
                   TownyMessaging.sendTownMessagePrefixed(a, fun.cstring(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
               }

               return true;
           }
           else
           {
               return  false;
           }
       }
       else
       {
           return false;
       }
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
       return  neutralslist.contains(t);
    }

    public boolean isNeutral(String name)
    {
        return  neutralslist.contains(name);
    }
}
