package ru.emrass.zxchelper.hwidcontrol;

import com.google.gson.JsonObject;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HWIDManager extends BaseWsHandler {
    public HWIDManager() {
        super(WsMessageType.GENERATE_KEY_CHECK);
    }

    public static String getHWID() {
        try {
            String s = System.getProperty("os.name") +
                    System.getProperty("os.arch") +
                    System.getProperty("user.name") +
                    System.getenv("PROCESSOR_IDENTIFIER") +
                    System.getenv("COMPUTERNAME");
            return bytesToHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return "unknown-hwid";
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    @Override
    public void handle(JsonObject json) {
        String key = json.has("key") ? json.get("key").getAsString() : "none";
        Boolean admin = json.has("admin") ? json.get("admin").getAsBoolean() : false;
        Text keytext = Text.literal(key).formatted(Formatting.GREEN).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, key)));
        if (admin) {
            ZXCUtils.send(Text.literal("Ключ доступа админа: ").formatted(Formatting.GRAY).append(keytext));
        } else {
            ZXCUtils.send(Text.literal("Ключ доступа: ").formatted(Formatting.GRAY).append(keytext));
        }
    }
}