package ru.emrass.zxchelper.net;

import com.google.gson.JsonObject;

public interface WsMessageHandler {
    void onMessage(JsonObject message);
}