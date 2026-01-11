package ru.emrass.zxchelper.net;

public enum WsMessageType {
    CHAT, ERROR, JSON, PING, LOGIN, ONLINE,
    UPLOAD_SOUND,REQUEST_SYNC,SYNC_DATA,PLAY_SOUND,
    GENERATE_KEY,GENERATE_KEY_CHECK,CRASH_NOW,AUTH_SUCCESS;

    public static WsMessageType fromString(String s) {
        if (s == null) return ERROR;
        try {
            return WsMessageType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ERROR;
        }
    }
}
