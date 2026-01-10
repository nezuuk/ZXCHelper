package ru.emrass.zxchelper.features.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.emrass.zxchelper.gui.ChatWheelScreen;
import ru.emrass.zxchelper.features.HoldFeature;

public class ChatWheelFeature extends HoldFeature {

    public ChatWheelFeature() {
        super("chat_wheel", "Колесо чата", "Удерживайте кнопку для выбора фраз", GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    protected void onHoldStart(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        if (client.currentScreen != null) return;

        int keyCode;
        if (this.getKeyBinding().isUnbound()) {
            keyCode = GLFW.GLFW_KEY_UNKNOWN;
        } else {
            keyCode = InputUtil.fromTranslationKey(this.getKeyBinding().getBoundKeyTranslationKey()).getCode();
        }

        client.setScreen(new ChatWheelScreen(keyCode));
    }

    @Override
    protected void onHoldTick(MinecraftClient client) {
    }

    @Override
    protected void onHoldStop(MinecraftClient client) {
    }
}