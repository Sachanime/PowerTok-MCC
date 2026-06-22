package com.skl.powertok.mcc.managers;

import org.bukkit.entity.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;

public class BossbarManager {

    public void displyaBossBar(Player player, String title, NamedTextColor titleColor, float progress, BossBar.Color color, BossBar.Overlay notchNumber) {

        Component welcomerBossBarTitle = MiniMessage.miniMessage().deserialize(title);
        welcomerBossBarTitle.color(titleColor);
        
        BossBar welcomerBossBar = BossBar.bossBar(welcomerBossBarTitle, progress, color, notchNumber);
        player.showBossBar(welcomerBossBar);

    }

    public void noDisplayBossBar(Player player, BossBar bossBar) {
        player.hideBossBar(bossBar);
    }

}