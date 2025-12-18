package ru.emrass.zxchelper.features;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import ru.emrass.zxchelper.ZXCHelper;


@Getter
public abstract class BaseFeature {

    private final String id;
    private final String displayName;
    private final String description;

    private final boolean keybound;
    private final int defaultKeyCode;

    protected KeyBinding keyBinding;


    protected BaseFeature(String id, String displayName, String description, int defaultKeyCode) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.keybound = true;
        this.defaultKeyCode = defaultKeyCode;
    }

    protected BaseFeature(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.keybound = false;
        this.defaultKeyCode = GLFW.GLFW_KEY_UNKNOWN;
    }

    public void onRegistered() {
    }

    public void onClientTick(MinecraftClient client) {
    }

    public void onKeyPressed(MinecraftClient client) {
    }

    void createKeyBinding(String categoryName) {
        if (!keybound) return;

        this.keyBinding = new KeyBinding(
                displayName,
                InputUtil.Type.KEYSYM,
                defaultKeyCode,
                categoryName
        );
    }



    protected void sendStatusMessage(boolean enabled) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX)
                .formatted(Formatting.GOLD);

        Text name = Text.literal(displayName)
                .formatted(Formatting.GRAY);

        Text colon = Text.literal(": ");

        Text status = Text.literal(enabled ? "Включен" : "Выключен")
                .formatted(enabled ? Formatting.GREEN : Formatting.RED);

        Text msg = prefix.copy()
                .append(name)
                .append(colon)
                .append(status);

        mc.player.sendMessage(msg);
    }
}