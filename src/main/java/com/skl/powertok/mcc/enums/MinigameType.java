package com.skl.powertok.mcc.enums;

public enum MinigameType {

    RAID("raid.");

    private final String minigame;

    MinigameType(String minigame) {
        this.minigame = minigame;
    }

    public String getMinigame() {
        return minigame;
    }
    
}
