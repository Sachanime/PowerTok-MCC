package com.skl.powertok.mcc.enums;

public enum MapType {
    
    DESERT_ISLAND_VILLAGE(-7648411007648063383L, 57, 63, 37);

    private final long seed;
    private final int xCrd;
    private final int yCrd;
    private final int zCrd;

    MapType(long seed, int xCrd, int yCrd, int zCrd) {
        this.seed = seed;
        this.xCrd = xCrd;
        this.yCrd = yCrd;
        this.zCrd = zCrd;
    }

    public long getSeed() {
        return(seed);
    }

    public int getXCrd() {
        return(xCrd);
    }

    public int getYCrd() {
        return(yCrd);
    }

    public int getZCrd() {
        return(zCrd);
    }

}
