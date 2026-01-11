package ru.emrass.zxchelper.features.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import ru.emrass.zxchelper.features.HoldFeature;
import ru.emrass.zxchelper.gui.SoundWheelScreen;


public class SoundWheelFeature extends HoldFeature {

    public SoundWheelFeature() {
        super("sound_wheel", "Колесо Звуков", "Меню выбора звуков", GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    protected void onHoldStart(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        int keyCode = this.getKeyBinding().getDefaultKey().getCode();
        if (!this.getKeyBinding().isUnbound()) {
            keyCode = InputUtil.fromTranslationKey(this.getKeyBinding().getBoundKeyTranslationKey()).getCode();
        }

        client.setScreen(new SoundWheelScreen(keyCode));
    }

    @Override
    protected void onHoldTick(MinecraftClient client) {}

    @Override
    protected void onHoldStop(MinecraftClient client) {}
}