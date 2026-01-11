package ru.emrass.zxchelper.net;

public enum WsMessageType {
    CHAT, ERROR, JSON, PING, LOGIN, ONLINE;

    public static WsMessageType fromString(String s) {
        if (s == null) return ERROR;
        try {
            return WsMessageType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ERROR;
        }
    }
}
