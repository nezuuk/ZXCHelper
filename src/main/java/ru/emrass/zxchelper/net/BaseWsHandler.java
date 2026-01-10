package ru.emrass.zxchelper.net;

import com.google.gson.JsonObject;

public abstract class BaseWsHandler {

    private final WsMessageType type;

    protected BaseWsHandler(WsMessageType type) {
        this.type = type;
    }

    public final WsMessageType getType() {
        return type;
    }

    public abstract void handle(JsonObject json);
}