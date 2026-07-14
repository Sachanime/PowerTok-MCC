package com.skl.powertok.mcc.managers;

import org.bukkit.entity.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;

public class BossbarManager {

    /**
     * Affiche une BossBar
     * 
     * @param player        Le joueur qui verra la BossBar
     * @param title         Le titre de la BossBar
     * @param titleColor    La couleur du titre 
     * @param progress      Le taux de remplissage de la BossBar
     * @param color         La couleur de la BossBar
     * @param notchNumber   Le nombre de section de la BossBar ({@code PROGRESS | NOTCHED_6 | NOTCHED_10 | NOTCHED_ 12 | NOTCHED_20})
     */
    public BossBar displayBossBar(Player player, String title, NamedTextColor titleColor, float progress, BossBar.Color color, BossBar.Overlay notchNumber) {

        Component bossBarTitle = MiniMessage.miniMessage().deserialize(title);
        bossBarTitle.color(titleColor);
        
        BossBar bossBar = BossBar.bossBar(bossBarTitle, progress, color, notchNumber);
        player.showBossBar(bossBar);

        return(bossBar);

    }

    /**
     * Supprime une BossBar
     * 
     * @param player    Le joueur qui a la BossBar
     * @param bossBar   La BossBar à supprimer
     */
    public void noDisplayBossBar(Player player, BossBar bossBar) {
        player.hideBossBar(bossBar);
    }

    /**
     * Modifier le titre d'une BossBar
     * 
     * @param bossbar       La BossBar à modifier
     * @param title         Le nouveau titre de la BossBar
     * @param titleColor    La couleur du titre
     */
    public void editBossBarName(BossBar bossbar, String title, NamedTextColor titleColor) {

        Component bossBarTitle = MiniMessage.miniMessage().deserialize(title);

        bossBarTitle.color(titleColor);
        bossbar.name(bossBarTitle);

    }

}