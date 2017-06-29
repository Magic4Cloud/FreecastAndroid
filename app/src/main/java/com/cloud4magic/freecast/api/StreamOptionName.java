package com.cloud4magic.freecast.api;

public enum StreamOptionName {
    NSO_RCVTIMEO(1),
    NSO_SNDTIMEO(2);

    private int numVal;

    StreamOptionName(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
