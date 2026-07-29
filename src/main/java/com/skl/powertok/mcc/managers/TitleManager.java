package com.skl.powertok.mcc.managers;

import java.time.Duration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TitleManager {
    
    public Title createTitle(String title, NamedTextColor titleColor, String subtitle, NamedTextColor subtitleColor) {

        Title.Times times = Title.Times.times(Duration.ofSeconds(1L), Duration.ofSeconds(3L), Duration.ofSeconds(1L));

        Component mainTitle = Component.text(title).color(titleColor);
        Component subTitle = Component.text(subtitle).color(subtitleColor);

        Title displayedTitle = Title.title(mainTitle, subTitle, times);

        return(displayedTitle);

    }

}
