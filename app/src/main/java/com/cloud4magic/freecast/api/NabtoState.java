package com.cloud4magic.freecast.api;

public enum NabtoState {
    NTCS_CLOSED,
    NTCS_CONNECTING,
    NTCS_READY_FOR_RECONNECT,
    NTCS_UNKNOWN,
    NTCS_LOCAL,
    NTCS_REMOTE_P2P,
    NTCS_REMOTE_RELAY,
    NTCS_REMOTE_RELAY_MICRO,
    FAILED;

    static NabtoState fromInteger(int nabtoState) {
    	nabtoState++;
        if (nabtoState < NabtoState.values().length && nabtoState >= 0) {
            return NabtoState.values()[nabtoState];
        } else {
            return FAILED;
        }
    }

    public String toString() {
        switch (this) {
            case NTCS_CLOSED: return "Closed";
            case NTCS_CONNECTING: return "Connecting...";
            case NTCS_READY_FOR_RECONNECT: return "Ready for reconnect";
            case NTCS_UNKNOWN: return "Unknown connection";
            case NTCS_LOCAL: return "Local";
            case NTCS_REMOTE_P2P: return "Remote P2P";
            case NTCS_REMOTE_RELAY: return "Remote relay";
            case NTCS_REMOTE_RELAY_MICRO: return "Remote relay micro";
            default: return "?";
        }
    }
}
