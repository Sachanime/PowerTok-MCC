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
     * Crée un nouveau Scoreboard
     * 
     * @return
     */
    public Scoreboard newScoreboard() {

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        return(scoreboard);

    }

    /**
     * Définit les données du Scoreboard
     * 
     * @param scoreboard
     * @param id
     * @param title
     * @return
     */
    public Objective setScoreboardObjective(Scoreboard scoreboard, String id, Component title) {

        Objective objective = scoreboard.registerNewObjective(id, Criteria.DUMMY, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        return(objective);

    }

    /**
     * Ajoute une ligne au Scoreboard
     * 
     * @param objective
     * @param score
     * @param scoreValue
     * @return
     */
    public Objective editScoreboardObjective(Objective objective, String score, int scoreValue) {

        objective.getScore(score).setScore(scoreValue);

        return(objective);

    }

    /**
     * Supprime une ligne au Scoreboard
     * 
     * @param scoreboard
     * @param score
     */
    public void removeScoreboardObjective(Scoreboard scoreboard, String score) {

        scoreboard.resetScores(score);
        
    }

    /**
     * Supprime le Scoreboard d'un joueur
     * 
     * @param player
     */
    @SuppressWarnings("null")
    public void deleteScoreboard(Player player) {

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(mainScoreboard);

    }

}
