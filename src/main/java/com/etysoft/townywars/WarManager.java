package com.etysoft.townywars;

import com.etysoft.townywars.events.WarDeclareEvent;
import com.etysoft.townywars.events.WarEndEvent;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarManager {

    private static  Set<Town> neutralslist = new HashSet<Town>();
    private static Map<String, Town> townswarlist = new ConcurrentHashMap<>();
    private static Map<Town, Town> addreq = new ConcurrentHashMap<>();
    private static Set<War> wars = new HashSet<War>();
    private static WarManager instance;

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

    public boolean isSendedJoinRequest(Town from)
    {
       return  addreq.containsKey(from);
    }

    public boolean hasJoinRequest(Town from, Town to)
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

    public void removeJoinRequest(Town from)
    {
        addreq.remove(from);
    }

    public boolean sendJoinRequest(Town from, Town to, Player pform)
    {

        if(!WarManager.getInstance().isInWar(from) && WarManager.getInstance().isInWar(to)) {

            if(!WarManager.getInstance().isNeutral(from) && WarManager.getInstance().getTownWar(to).isSide(to)) {
                Player p = Bukkit.getPlayer(to.getMayor().getName());

                if (p != null) {
                    p.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-req").replace("%s", from.getName())));
                    addreq.put(from, to);
                    pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-sendreq")));
                    return true;
                } else {
                    pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-mayoroffline")));
                    return false;
                }


            }
            else
            {
                pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-ntown")));
                return false;
            }
        }
        else
        {
            pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-ninwar")));
            return false;
        }
    }

    public Set<Town> getNeutralTowns()
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
        WarEndEvent warEndEvent = new WarEndEvent(w);

        TownyWars.callEvent(warEndEvent);

        if(!warEndEvent.isCancelled()) {
            if (!withpain) {
                wars.remove(w);
                if (townswarlist.containsKey(w.getVictim().getName())) {
                    townswarlist.remove(w.getVictim().getName());
                }
                if (townswarlist.containsKey(w.getAttacker().getName())) {
                    townswarlist.remove(w.getAttacker().getName());
                }
                for (Town t : w.getATowns()) {
                    t.setAdminEnabledPVP(false);
                    townswarlist.remove(t.getName());
                }
                for (Town t : w.getVTowns()) {
                    t.setAdminEnabledPVP(false);
                    townswarlist.remove(t.getName());
                }
                w.getAttacker().setAdminEnabledPVP(false);
                w.getVictim().setAdminEnabledPVP(false);
                w.clear();
            } else {
                Town proig = w.getZeroPointTown();
                Town win = w.getNotZeroPointTown();

                if (proig != null) {


                    if (!TownyWars.instance.getConfig().getBoolean("only-town-delete")) {
                        List<TownBlock> tbs = null;
                        try {
                            tbs = new ArrayList<>(proig.getTownBlocks());
                        } catch (Exception e) {
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

                    TownyMessaging.sendPrefixedTownMessage(proig, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-lose")));
                    TownyMessaging.sendPrefixedTownMessage(win, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-win")));
                    w.clear();
                    wars.remove(w);
                    for (Town t : w.getATowns()) {
                        t.setAdminEnabledPVP(false);
                        townswarlist.remove(t.getName());
                    }
                    for (Town t : w.getVTowns()) {
                        t.setAdminEnabledPVP(false);
                        townswarlist.remove(t.getName());
                    }
                    if (townswarlist.containsKey(w.getVictim().getName())) {
                        townswarlist.remove(w.getVictim().getName());
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
                w.addVTown(t);
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

               WarDeclareEvent warDeclareEvent = new WarDeclareEvent(a, j);
               TownyWars.callEvent(warDeclareEvent);

               if(!warDeclareEvent.isCancelled()) {

                   a.setAdminEnabledPVP(true);
                   j.setAdminEnabledPVP(true);
                   wars.add(new War(a, j, this));

                   if (TownyWars.instance.getConfig().getInt("public-announce-warstart") == 2) {
                       Bukkit.broadcastMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
                   } else if (TownyWars.instance.getConfig().getInt("public-announce-warstart") == 1) {
                       TownyMessaging.sendTownMessagePrefixed(j, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
                       TownyMessaging.sendTownMessagePrefixed(a, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-declare").replace("%s", a.getName()).replace("%j", j.getName())));
                   }

                   return true;
               }
               else
               {
                   return false;
               }
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

}
