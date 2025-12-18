package ru.emrass.zxchelper.events;

public enum ChatChannel {
    GAME,   // игровые сообщения (join/leave, системка от сервера)
    CHAT,   // обычные чат-сообщения игроков
    HUD     // action bar / над хотбаром (overlay=true)
}
