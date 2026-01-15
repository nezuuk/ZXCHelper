package ru.emrass.zxchelper.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.hwidcontrol.HWIDManager;
import ru.emrass.zxchelper.hwidcontrol.Role;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.net.URI;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
public class WsService {

    @Getter
    private WebSocketClient client;

    private volatile boolean connecting = false;
    private volatile boolean shouldReconnect = true;

    private final Map<WsMessageType, WsMessageHandler> handlers =
            new EnumMap<>(WsMessageType.class);

    public void registerHandler(WsMessageType type, WsMessageHandler handler) {
        handlers.put(type, handler);
    }

    public void start() {
        shouldReconnect = true;
        connectAsync(0);
        registerHandler(WsMessageType.CRASH_NOW,message -> {
            ZXCUtils.send("pidoras....");
        });
        registerHandler(WsMessageType.ERROR_MESSAGE, message -> {
            String error = message.has("error") ? message.get("error").getAsString() : "error?";
            ZXCUtils.send(error);
        });
        registerHandler(WsMessageType.AUTH_SUCCESS, message -> {
            String role = message.has("role") ? message.get("role").getAsString() : "нету";
            Text text = Text.literal(role).formatted(role.equalsIgnoreCase("OWNER") ? Formatting.RED : role.equalsIgnoreCase("ADMIN") ? Formatting.DARK_RED : Formatting.GRAY);
            ZXCHelper.getInstance().setPlayerRole(Role.valueOf(role));
            ZXCUtils.send(Text.literal("Вы успешно авторизовались, роль: ").formatted(Formatting.GREEN).append(text));
        });
    }

    public void stop() {
        shouldReconnect = false;
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void send(WsMessageType type, JsonObject json) {
        json.addProperty("type", type.name());
        sendRaw(json.toString());
    }

    private void sendRaw(String raw) {
        if (client != null && client.isOpen()) {
            client.send(raw);
        } else {
            ZXCUtils.send("Нет соединения с WebSocket-сервером.", Formatting.RED);
        }
    }

    private void connectAsync(int delaySeconds) {
        if (!shouldReconnect) return;
        if (connecting) return;

        connecting = true;

        new Thread(() -> {
            try {
                if (delaySeconds > 0) {
                    Thread.sleep(delaySeconds * 1000L);
                }

                URI uri = new URI("wss://azerusclientserver-1.onrender.com");

                WebSocketClient ws = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        log.info("[{}] WebSocket connection established", ZXCHelper.MOD_NAME);
                        String user = MinecraftClient.getInstance().getSession().getUsername();
                        String hwid = HWIDManager.getHWID();
                        String key = ConfigManager.getConfig().getLicenseKey();
                        JsonObject raw = new JsonObject();
                        raw.addProperty("username", user);
                        raw.addProperty("hwid",hwid);
                        if (key != null && !key.isEmpty()) {
                            raw.addProperty("key", key.trim());
                        }
                        WsService.this.send(WsMessageType.LOGIN,raw);
                        ZXCUtils.send("Соединение с WebSocket установлено.", Formatting.GRAY);
                    }

                    @Override
                    public void onMessage(String message) {
                        handleIncoming(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        log.info("[{}] WebSocket closed: code={}, reason='{}', remote={}",
                                ZXCHelper.MOD_NAME, code, reason, remote);
                        ZXCUtils.send("Соединение с WebSocket закрыто: " + reason, Formatting.GRAY);
                        if (shouldReconnect) {
                            connectAsync(5);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        log.error("[{}] WebSocket error", ZXCHelper.MOD_NAME, ex);
                        ZXCUtils.send("Ошибка WebSocket: " + ex.getClass().getSimpleName()
                                + ": " + ex.getMessage(), Formatting.RED);
                        if (shouldReconnect && !isOpen()) {
                            connectAsync(10);
                        }
                    }
                };

                ws.setConnectionLostTimeout(0);
                this.client = ws;
                log.info("[{}] Connecting to WebSocket {} ...", ZXCHelper.MOD_NAME, uri);
                ws.connectBlocking();

            } catch (Exception e) {
                log.error("[{}] Failed to connect to WebSocket", ZXCHelper.MOD_NAME, e);
                ZXCUtils.send("Не удалось подключиться к WebSocket: "
                        + e.getClass().getSimpleName() + ": " + e.getMessage(), Formatting.RED);
                if (shouldReconnect) {
                    connectAsync(10);
                }
            } finally {
                connecting = false;
            }
        }, "ZXCHelper-WsConnect").start();
    }

    private void handleIncoming(String message) {
        JsonObject root;
        try {
            root = JsonParser.parseString(message).getAsJsonObject();
        } catch (Exception e) {
            JsonObject raw = new JsonObject();
            raw.addProperty("type", WsMessageType.ERROR.name());
            raw.addProperty("msg", message);
            dispatch(WsMessageType.ERROR, raw);
            return;
        }

        String typeStr = root.has("type") ? root.get("type").getAsString() : null;
        WsMessageType type = WsMessageType.fromString(typeStr);
        dispatch(type, root);
    }

    private void dispatch(WsMessageType type, JsonObject json) {
        WsMessageHandler handler = handlers.get(type);
        if (handler != null) {
            try {
                handler.onMessage(json);
            } catch (Exception e) {
                log.error("[{}] Error in handler for type {}", ZXCHelper.MOD_NAME, type, e);
            }
        }
    }
}