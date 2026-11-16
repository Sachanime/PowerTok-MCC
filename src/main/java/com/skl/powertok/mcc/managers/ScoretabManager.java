package com.skl.powertok.mcc.managers;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

public class ScoretabManager {

    /**
     * Create a new Scoreboard
     * 
     * @return The Scoreboard
     */
    public Scoreboard newScoreboard() {

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        return(scoreboard);

    }

    /**
     * Define Scoreboard data
     * 
     * @param scoreboard    Target Scoreboard
     * @param id            Scoreboard ID
     * @param title         Scoreboard title
     * @return              Scorebaord data object
     */
    public Objective setScoreboardObjective(Scoreboard scoreboard, String id, Component title) {

        Objective objective = scoreboard.registerNewObjective(id, Criteria.DUMMY, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        return(objective);

    }

    /**
     * Add line to a Scoreboard
     * 
     * @param objective     Scoreboard data object
     * @param score         Line text
     * @param scoreValue    Line position
     * @return              New Scorebaord data object
     */
    public Objective editScoreboardObjective(Objective objective, String score, int scoreValue) {

        objective.getScore(score).setScore(scoreValue);

        return(objective);

    }

    /**
     * Delete Scorebaord line
     * 
     * @param scoreboard    Target Scoreboard
     * @param score         Line
     */
    public void removeScoreboardObjective(Scoreboard scoreboard, String score) {

        scoreboard.resetScores(score);
        
    }

    /**
     * Delete Scorebord from a player
     * 
     * @param player Target player
     */
    public void deleteScoreboard(Player player) {

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if(mainScoreboard == null) {
            return;
        }

        player.setScoreboard(mainScoreboard);

    }

}
