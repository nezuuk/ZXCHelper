package ru.emrass.zxchelper.features;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public abstract class ToggleFeature extends BaseFeature {

    @Getter
    private boolean enabled = false;

    protected ToggleFeature(String id, String displayName, String description, int defaultKeyCode) {
        super(id, displayName, description, defaultKeyCode);
    }

    @Override
    public final void onKeyPressed(MinecraftClient client) {
        toggle(client);
    }

    public final void toggle(MinecraftClient client) {
        setEnabled(!enabled, client, true);
    }


    public final void setEnabled(boolean value, MinecraftClient client, boolean showStatus) {
        if (this.enabled == value) return;
        this.enabled = value;

        if (showStatus) {
            sendStatusMessage(this.enabled);
        }

        if (this.enabled) {
            onEnabled(client);
        } else {
            onDisabled(client);
        }
    }

    @Override
    public final void onClientTick(MinecraftClient client) {
        if (enabled) {
            onEnabledTick(client);
        }
    }

    protected void onEnabled(MinecraftClient client) {
    }

    protected void onDisabled(MinecraftClient client) {
    }

    protected void onEnabledTick(MinecraftClient client) {
    }
}