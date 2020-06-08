package com.etysoft.townywars;

import com.palmergames.bukkit.towny.object.Town;

public class War {

   private Town attacker;
   private Town jertva;
   private Integer apoints;
   private Integer jpoints;
   private WarManager wm;

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
        if(t == attacker)
        {
            minusA();

        }
        if(t == jertva)
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
