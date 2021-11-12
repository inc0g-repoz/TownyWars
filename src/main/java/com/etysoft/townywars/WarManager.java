package com.etysoft.townywars;

import com.etysoft.townywars.events.NeutralityChangeEvent;
import com.etysoft.townywars.events.WarDeclareEvent;
import com.etysoft.townywars.events.WarEndEvent;
import com.etysoft.townywars.events.WarJoinEvent;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarManager {
    private static  Set<Town> neutralslist = new HashSet<>();
    private static Map<String, Town> townswarlist = new ConcurrentHashMap<>();
    private static Map<Town, Town> addreq = new ConcurrentHashMap<>();
    private static Set<War> wars = new HashSet<>();
    private static WarManager instance;

    public WarManager() {
        neutralslist = DataManager.loadNeutrals();
        instance = this;
    }

    public War getTownWar(Town t) {
        for (War w : wars) {
            if (w.hasTown(t)) {
                return w;
            }
        }

        return null;
    }

    public boolean isSendedJoinRequest(Town from) {
       return addreq.containsKey(from);
    }

    public boolean hasJoinRequest(Town from, Town to) {
        if(addreq.containsKey(from)) {
            return addreq.get(from) == to;
        } else {
            return false;
        }
    }

    public void removeJoinRequest(Town from) {
        addreq.remove(from);
    }

    public void sendJoinRequest(Town from, Town to, Player pform) {
        if(!WarManager.getInstance().isInWar(from) && WarManager.getInstance().isInWar(to)) {
            if(!WarManager.getInstance().isNeutral(from) && WarManager.getInstance().getTownWar(to).isSide(to)) {
                Player p = Bukkit.getPlayer(to.getMayor().getName());
                if (p != null) {
                    p.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-req")).replace("%s", from.getName())));
                    addreq.put(from, to);
                    pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-sendreq")));
                } else {
                    pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-mayoroffline")));
                }
            } else {
                pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-ntown")));
            }
        } else {
            pform.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-ninwar")));
        }
    }

    public Set<Town> getNeutralTowns() {
        return neutralslist;
    }

    public Set<War> getWars() {
        return wars;
    }

    public static WarManager getInstance() {
        return  instance;
    }

    public void end(War w, boolean withpain) {
        WarEndEvent warEndEvent = new WarEndEvent(w);
        TownyWars.callEvent(warEndEvent);
        Town proig = w.getZeroPointTown();
        Town win = w.getNotZeroPointTown();

        if (TownyWars.instance.discord) {
            TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-end-lose")).replace("%loser%", proig.getName()).replace("%winner%", win.getName()));
            TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-end-win")).replace("%loser%", proig.getName()).replace("%winner%", win.getName()));
        }

        if (!warEndEvent.isCancelled()) {
            if (withpain) {
                String action = TownyWars.instance.getConfig().getString("lose-action");
                assert action != null;
                switch (action) {
                    case "delete":
                        if (proig != null) {
                            if (!TownyWars.instance.getConfig().getBoolean("only-town-delete")) {
                                List<TownBlock> tbs = null;
                                try {
                                    tbs = new ArrayList<>(proig.getTownBlocks());
                                } catch (Exception e) {
                                    Bukkit.getConsoleSender().sendMessage("Error! Please report this to Discord https://discord.gg/Etd4XXH");
                                }

                                assert tbs != null;
                                List<TownBlock> tList = new ArrayList<>(tbs);

                                for (TownBlock tb : tList) {
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

                            TownyUniverse.getInstance().getDataSource().deleteTown(proig);
                            TownyUniverse.getInstance().getDataSource().removeTown(proig);
                        } else {
                            Bukkit.getConsoleSender().sendMessage("Proig is null!");
                        }
                        break;
                    case "steal":
                        try {
                            win.setAdminEnabledPVP(false);
                            proig.setAdminEnabledPVP(false);
                            TownyMessaging.sendPrefixedTownMessage(proig, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-lose")));
                            TownyMessaging.sendPrefixedTownMessage(win, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-win")));
                            if (proig.getAccount().getHoldingBalance() >= 1) {
                                win.getAccount().setBalance(win.getAccount().getHoldingBalance() + proig.getAccount().getHoldingBalance(), "War end");
                                proig.getAccount().setBalance(0, "War steal");
                            } else if (TownyWars.instance.getConfig().getBoolean("delete-if-cant-steal")) {
                                TownyUniverse.getInstance().getDataSource().deleteTown(proig);
                                TownyUniverse.getInstance().getDataSource().removeTown(proig);
                            }
                        } catch (Exception e) {
                            Bukkit.getConsoleSender().sendMessage("[TownyWars] Error stealing bank:");
                            e.printStackTrace();
                        }
                        break;
                    case "prize":
                        TownyMessaging.sendPrefixedTownMessage(proig, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-lose")));
                        TownyMessaging.sendPrefixedTownMessage(win, ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-win")));
                        win.setAdminEnabledPVP(false);
                        proig.setAdminEnabledPVP(false);
                        try {
                            win.getAccount().setBalance(win.getAccount().getHoldingBalance() + TownyWars.instance.getConfig().getDouble("prize"), "War end");
                        } catch (Exception e) {
                            Bukkit.getConsoleSender().sendMessage("[TownyWars] Error depositing prize:");
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage("Can't find end action! (delete, prize or steal) Is config.yml is outdated?");
                        break;
                }
            }
        }

        // Removing victim
        w.getVictim().setAdminEnabledPVP(false);
        townswarlist.remove(w.getVictim().getName());

        // Removing the attacker
        w.getAttacker().setAdminEnabledPVP(false);
        townswarlist.remove(w.getAttacker().getName());

        // Removing other towns were involved too
        for (Town t : w.getATowns()) {
            t.setAdminEnabledPVP(false);
            townswarlist.remove(t.getName());
        }
        for (Town t : w.getVTowns()) {
            t.setAdminEnabledPVP(false);
            townswarlist.remove(t.getName());
        }

        wars.remove(w);
        w.clear();
    }

    public void setNeutrality(Boolean neutrality, Town t) {
        NeutralityChangeEvent neutralityChangeEvent = new NeutralityChangeEvent(t, neutrality);
        TownyWars.callEvent(neutralityChangeEvent);
        if (!neutralityChangeEvent.isCancelled()) {
            if (neutrality) {
                if (!neutralslist.contains(t)) {
                    neutralslist.add(t);
                    if (TownyWars.instance.discord) {
                        TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-neutral")).replace("%town%", t.getName()));
                    }
                }
            } else {
                if (neutralslist.contains(t)) {
                    neutralslist.remove(t);
                    if (TownyWars.instance.discord) {
                        TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-noneutral")).replace("%town%", t.getName()));
                    }
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("[TownyWars] The change of neutrality was canceled by another plugin.");
        }
    }

    public void addTownToWar(Town t, War w, boolean isAside) {
        if(!isInWar(t)) {
            t.setAdminEnabledPVP(true);
            WarJoinEvent warJoinEvent = new WarJoinEvent(w, t, isAside);
            TownyWars.callEvent(warJoinEvent);
            if (!warJoinEvent.isCancelled()) {
                if (isAside) {
                    w.addATown(t);
                    if (TownyWars.instance.discord) {
                        TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-join-war")).replace("%town%", t.getName()).replace("%victim%", w.getVictim().getName()));
                    }
                } else {
                    w.addVTown(t);
                    if (TownyWars.instance.discord) {
                        TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-join-war")).replace("%town%", t.getName()).replace("%victim%", w.getAttacker().getName()));
                    }
                }
                addTownToWarList(t);
            } else {
                Bukkit.getConsoleSender().sendMessage("[TownyWars] Joining the war was canceled by another plugin.");
            }

        }
    }


    public boolean declare(Town a, Town j) {
        if (a != j) {
            if (!WarManager.getInstance().isInWar(a) && !WarManager.getInstance().isInWar(j)) {
                WarDeclareEvent warDeclareEvent = new WarDeclareEvent(a, j);
                TownyWars.callEvent(warDeclareEvent);
                if (!warDeclareEvent.isCancelled()) {
                    a.setAdminEnabledPVP(true);
                    j.setAdminEnabledPVP(true);
                    War war = new War(a, j, this);

                    if (TownyWars.instance.getConfig().getBoolean("sidebar")) {
                        Sidebar.create(war);
                    }

                    wars.add(war);
                    if (TownyWars.instance.getConfig().getInt("public-announce-warstart") == 2) {
                        Bukkit.broadcastMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-declare")).replace("%s", a.getName()).replace("%j", j.getName())));
                        if (TownyWars.instance.discord) {
                            TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-war-declare")).replace("%attacker%", a.getName()).replace("%victim%", j.getName()));
                        }
                    } else if (TownyWars.instance.getConfig().getInt("public-announce-warstart") == 1) {
                        TownyMessaging.sendTownMessagePrefixed(j, ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-declare")).replace("%s", a.getName()).replace("%j", j.getName())));
                        TownyMessaging.sendTownMessagePrefixed(a, ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-declare")).replace("%s", a.getName()).replace("%j", j.getName())));
                        if(TownyWars.instance.discord) {
                            TownyWars.instance.sendDiscord(Objects.requireNonNull(TownyWars.instance.getConfig().getString("message-war-declare")).replace("%attacker%", a.getName()).replace("%victim%", j.getName()));
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return  false;
            }
        } else {
            return false;
        }
    }


    public void addTownToWarList(Town t) {
        townswarlist.put(t.getName(), t);
    }

    public boolean isInWar(Town t) {
        return  townswarlist.containsKey(t.getName());
    }

    public boolean isNeutral(Town t) {
       return  neutralslist.contains(t);
    }

}
