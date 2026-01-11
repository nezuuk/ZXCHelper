package ru.emrass.zxchelper.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;

@UtilityClass
public class ZXCUtils {

    public void sendWithPrefix(String prefix, String msg, Formatting color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player != null) {
                Text p = Text.literal(prefix).formatted(Formatting.GOLD);
                Text t = Text.literal(msg).formatted(color);
                mc.player.sendMessage(p.copy().append(t));
            }
        });
    }

    public void send(String msg, Formatting color) {
        sendWithPrefix(ZXCHelper.CHAT_PREFIX, msg, color);
    }


    public void send(String msg) {
        sendWithPrefix(ZXCHelper.CHAT_PREFIX, msg, Formatting.GRAY);
    }

    public void send(Text text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player != null) {
                mc.player.sendMessage(text);
            }
        });
    }}