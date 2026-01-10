package ru.emrass.zxchelper.features;

import net.minecraft.client.MinecraftClient;

public abstract class HoldFeature extends BaseFeature {

    private boolean isHeld = false;

    protected HoldFeature(String id, String displayName, String description, int defaultKeyCode) {
        super(id, displayName, description, defaultKeyCode);
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (this.keyBinding == null) return;

        boolean currentlyPressed = this.keyBinding.isPressed();

        if (currentlyPressed) {
            if (!isHeld) {
                isHeld = true;
                onHoldStart(client);
            }
            onHoldTick(client);
        } else {
            if (isHeld) {
                isHeld = false;
                onHoldStop(client);
            }
        }
    }

    @Override
    public final void onKeyPressed(MinecraftClient client) {
    }


    protected abstract void onHoldStart(MinecraftClient client);

    protected void onHoldTick(MinecraftClient client) {}

    protected abstract void onHoldStop(MinecraftClient client);
}