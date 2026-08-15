package com.skl.powertok.mcc.managers;

import org.jspecify.annotations.NonNull;
import org.bukkit.entity.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;

public class BossbarManager {

    /**
     * Display a BossBar to a player
     * 
     * @param player        Target player
     * @param title         BossBar title
     * @param titleColor    BossBar title color
     * @param progress      BossBar fill level
     * @param color         BossBar color
     * @param notchNumber   BossBar section number ({@code PROGRESS | NOTCHED_6 | NOTCHED_10 | NOTCHED_ 12 | NOTCHED_20})
     * 
     * @return              The BossBar
     */
    public BossBar displayBossBar(Player player, @NonNull String title, NamedTextColor titleColor, float progress, BossBar.@NonNull Color color, BossBar.@NonNull Overlay notchNumber) {

        Component bossBarTitle = MiniMessage.miniMessage().deserialize(title).color(titleColor);
        
        BossBar bossBar = BossBar.bossBar(bossBarTitle, progress, color, notchNumber);
        player.showBossBar(bossBar);

        return(bossBar);

    }

    /**
     * Hide a BossBar from a player
     * 
     * @param player    Le joueur qui a la BossBar
     * @param bossBar   La BossBar à supprimer
     */
    public void noDisplayBossBar(Player player, @NonNull BossBar bossBar) {
        player.hideBossBar(bossBar);
    }

    /**
     * Edit a BossBar title
     * 
     * @param bossbar       Target BossBar
     * @param title         BossBar new title
     * @param titleColor    BossBar title color
     */
    public void editBossBarName(BossBar bossbar, @NonNull String title, NamedTextColor titleColor) {

        Component bossBarTitle = MiniMessage.miniMessage().deserialize(title);

        bossBarTitle.color(titleColor);
        bossbar.name(bossBarTitle);

    }

}