package com.etysoft.townywars;

import com.palmergames.bukkit.towny.object.Town;

import java.util.HashSet;
import java.util.Set;

public class War {

   private Town attacker;
   private Town jertva;
   private Integer apoints;
   private Integer jpoints;
   private WarManager wm;
    private static Set<Town> asidetowns = new HashSet<Town>();
    private static Set<Town> jsidetowns = new HashSet<Town>();

    public War(Town a, Town j, WarManager warm)
    {
        apoints = a.getNumResidents();
        jpoints = j.getNumResidents();
        attacker = a;
        jertva = j;
        wm = warm;
        wm.addTownToWarList(a);
        wm.addTownToWarList(j);
    }

    public boolean isASide(Town t) throws Exception {
        if(asidetowns.contains(t))
        {
            return true;
        }
        else if(jsidetowns.contains(t))
        {
            return  false;
        }
     else
        {
            throw new Exception();
        }

    }

    public void addATown(Town t)
    {
        asidetowns.add(t);
    }

    public void removeATown(Town t)
    {
        asidetowns.remove(t);
    }

    public void addJTown(Town t)
    {
        jsidetowns.add(t);
    }

    public void removeJTown(Town t)
    {
        jsidetowns.remove(t);
    }

    public  Set<Town> getJTowns()
    {
        return jsidetowns;
    }

    public  Set<Town> getATowns()
    {
        return asidetowns;
    }

    public Town getAttacker()
    {
        return  attacker;
    }

    public Town getJertva()
    {
        return  jertva;
    }

    public Town getZeroPointTown()
    {
        if(jpoints == 0)
        {
            return jertva;
        }
        if(apoints == 0)
        {
            return attacker;
        }
        return  null;
    }
    public Town getNotZeroPointTown()
    {
        if(jpoints == 0)
        {
            return attacker;
        }
        if(apoints == 0)
        {
            return jertva;
        }
        return  null;
    }

    //if not null == war end (return victor)
    public Town minus(Town t)
    {
        if(t == attacker || asidetowns.contains(t))
        {
            minusA();

        }
        if(t == jertva || jsidetowns.contains(t))
        {
            minusJ();
        }
        if(jpoints == 0)
        {
            return attacker;
        }
        if(apoints == 0)
        {
            return jertva;
        }
        return  null;
    }

    public boolean hasTown(Town t)
    {
        if(attacker == t)
        {
            return  true;
        }
        if(jertva == t)
        {
            return  true;
        }
        if(asidetowns.contains(t))
        {
            return true;
        }
        if(jsidetowns.contains(t))
        {
            return true;
        }
        return false;
    }

    public void minusJ()
    {
        jpoints = jpoints - 1;
        apoints = apoints + 1;
    }

    public void minusA()
    {
        apoints = apoints - 1;
        jpoints = jpoints + 1;
    }

    public int getAPoints()
    {
        return apoints;
    }

    public int getJPoints()
    {
        return jpoints;
    }
}
