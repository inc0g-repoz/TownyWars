package com.etysoft.townywars;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import com.palmergames.bukkit.towny.object.Resident;

public class Sidebar {

    War war;
    Scoreboard scoreboard;
    Objective objective;
    int taskId;

    private Sidebar(War war) {
        this.war = war;
        scoreboard = TownyWars.instance.getServer().getScoreboardManager().getNewScoreboard();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TownyWars.instance, task, 0L, 20L);
    }

    private void updateScore(String town, int points) {
        try {
            objective.getScore(town).setScore(points);
        } catch (Exception e) {
        	TownyWars.instance.getLogger().warning(e.toString());
        }
    }

    private void updatePlayerScoreboard(Resident r) {
        if (r.getPlayer() != null) {
            r.getPlayer().setScoreboard(scoreboard);
        }
    }

    public static Sidebar create(War war) {
        return new Sidebar(war);
    }

    public void setup() {
        if (objective != null) {
            return;
        }
        objective = scoreboard.registerNewObjective("positions", "dummy", "Points", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void remove() {
        if (objective == null) {
            return;
        }
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        objective.unregister();
        objective = null;
    }

    private Runnable task = () -> {
        if (taskId == 0) {
            return;
        }

        if (!WarManager.getInstance().getWars().contains(war)) {
            remove();
            return;
        }

        setup();

        // Updating scores for towns
        updateScore(war.getAttacker().getName(), war.getAPoints());
        updateScore(war.getVictim().getName(), war.getVPoints());

        // Rendering scores for all users
        war.getAttacker().getResidents().forEach(r -> updatePlayerScoreboard(r));
        war.getVictim().getResidents().forEach(r -> updatePlayerScoreboard(r));
        war.getATowns().forEach(t -> t.getResidents().forEach(r -> updatePlayerScoreboard(r)));
        war.getVTowns().forEach(t -> t.getResidents().forEach(r -> updatePlayerScoreboard(r)));
    };

}
