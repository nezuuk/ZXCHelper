package ru.emrass.zxchelper.features;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;

/**
 * Базовый класс для ВКЛ/ВЫКЛ фич с биндом.
 * Сам:
 *  - хранит enabled;
 *  - по нажатию бинда переключает enabled и выводит статус;
 *  - вызывает onEnabled()/onDisabled();
 *  - на каждом тике, если включено, вызывает onEnabledTick().
 */
public abstract class ToggleFeature extends BaseFeature {

    @Getter
    private boolean enabled = false;

    protected ToggleFeature(String id, String displayName, String description, int defaultKeyCode) {
        super(id, displayName, description, defaultKeyCode);
    }

    /** Нажали бинд — просто переключаем. */
    @Override
    public final void onKeyPressed(MinecraftClient client) {
        toggle(client);
    }

    /** Переключить состояние (с выводом статуса). */
    public final void toggle(MinecraftClient client) {
        setEnabled(!enabled, client, true);
    }

    /**
     * Программно включить/выключить фичу.
     * @param value       новое значение enabled
     * @param client      клиент
     * @param showStatus  показывать ли сообщение в чате
     */
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

    /** Один раз при включении. */
    protected void onEnabled(MinecraftClient client) {
    }

    /** Один раз при выключении. */
    protected void onDisabled(MinecraftClient client) {
    }

    /** Каждый тик, пока фича включена. */
    protected void onEnabledTick(MinecraftClient client) {
    }
}