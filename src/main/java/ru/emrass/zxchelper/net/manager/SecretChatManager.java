package ru.emrass.zxchelper.net.manager;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.net.WsService;
import ru.emrass.zxchelper.utils.ZXCUtils;

public final class SecretChatManager extends BaseWsHandler {

    private final WsService wsService;

    public SecretChatManager(WsService wsService) {
        super(WsMessageType.CHAT);
        this.wsService = wsService;


        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if(ConfigManager.getConfig().isActivePrefixChat()) {
                if (message.startsWith("#") || message.startsWith("№")) {

                    String content = message.substring(1).trim();

                    if (content.isEmpty()) {
                        return false;
                    }

                    sendChat(content);

                    return false;
                }

                return true;
            }else {
                return true;
            }
        });
    }

    public void sendChat(String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSession() == null) {
            return;
        }
        String nick = mc.getSession().getUsername();

        JsonObject json = new JsonObject();
        json.addProperty("player", nick);
        json.addProperty("text", text);

        wsService.sendJson(WsMessageType.CHAT, json);
    }

    @Override
    public void handle(JsonObject json) {
        String nick = json.has("player") ? json.get("player").getAsString() : "???";
        String text = json.has("text") ? json.get("text").getAsString() : "";

        Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
        Text nickText = Text.literal(nick).formatted(Formatting.GRAY);
        Text colon = Text.literal(": ");
        Text msgText = Text.literal(text).formatted(Formatting.WHITE);

        Text full = prefix.copy()
                .append(nickText)
                .append(colon)
                .append(msgText);

        ZXCUtils.send(full);
    }
}