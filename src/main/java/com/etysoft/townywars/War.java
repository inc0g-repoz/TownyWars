package com.etysoft.townywars;

import com.palmergames.bukkit.towny.object.Town;

import java.util.HashSet;
import java.util.Set;

public class War {
    private Town attacker;
    private Town victim;
    private Integer apoints;
    private Integer vpoints;
    public Town fromreqtown = null;
    private WarManager wm;
    private static Set<Town> asidetowns = new HashSet<Town>();
    private static Set<Town> vsidetowns = new HashSet<Town>();
    private boolean isCreated = false;

    public War(Town a, Town v, WarManager warm) {
        if (!isCreated) {
            apoints = a.getNumResidents();
            vpoints = v.getNumResidents();
            attacker = a;
            victim = v;
            wm = warm;
            asidetowns.add(a);
            vsidetowns.add(v);
            wm.addTownToWarList(a);
            wm.addTownToWarList(v);
            isCreated = true;
        } else {
            War w = null;
        }
    }

    public void clear() {
        asidetowns.clear();
        vsidetowns.clear();
        fromreqtown = null;
    }

    public Town getOppositeTown(Town side) {
        if (attacker == side) {
            return victim;
        }

        if (victim == side) {
            return attacker;
        }

        return null;
    }

    public boolean isSide(Town t) {
        if (attacker == t) {
            return true;
        }

        return victim == t;
    }

    public boolean isASide(Town t) throws Exception {
        if (asidetowns.contains(t)) {
            return true;
        } else if (vsidetowns.contains(t)) {
            return false;
        } else {
            throw new Exception();
        }

    }

    public void addATown(Town t) {
        asidetowns.add(t);
    }

    public void addVTown(Town t) {
        vsidetowns.add(t);
    }

    public Set<Town> getVTowns() {
        return vsidetowns;
    }

    public Set<Town> getATowns() {
        return asidetowns;
    }

    public Town getAttacker() {
        return attacker;
    }

    public Town getVictim() {
        return victim;
    }

    public Town getZeroPointTown() {
        if (vpoints == 0) {
            return victim;
        }

        if (apoints == 0) {
            return attacker;
        }

        return null;
    }

    public Town getNotZeroPointTown() {
        if (vpoints == 0) {
            return attacker;
        }

        if (apoints == 0) {
            return victim;
        }

        return null;
    }

    //if not null == war end (return victor)
    public Town minus(Town t) {
        if (t == attacker || asidetowns.contains(t)) {
            minusA();
        }

        if (t == victim || vsidetowns.contains(t)) {
            minusV();
        }

        if (vpoints == 0) {
            return attacker;
        }

        if (apoints == 0) {
            return victim;
        }

        return null;
    }

    public boolean hasTown(Town t) {
        if (attacker == t) {
            return true;
        }

        if (victim == t) {
            return true;
        }

        if (asidetowns.contains(t)) {
            return true;
        }

        return vsidetowns.contains(t);
    }

    public void minusV() {
        vpoints = vpoints - 1;
        apoints = apoints + 1;
    }

    public void minusA() {
        apoints = apoints - 1;
        vpoints = vpoints + 1;
    }

    public int getAPoints() {
        return apoints;
    }

    public int getVPoints() {
        return vpoints;
    }
}
