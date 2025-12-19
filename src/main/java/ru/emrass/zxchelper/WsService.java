package ru.emrass.zxchelper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class WsService {

    @Getter
    private WebSocketClient client;

    private volatile boolean connecting = false;
    private volatile boolean shouldReconnect = true;

    private static final String SYS_JOIN = "\u0000ZXCHelper_JOIN";
    private static final String SYS_LEAVE = "\u0000ZXCHelper_LEAVE";

    public void start() {
        shouldReconnect = true;
        connectAsync(0);
    }

    public void stop() {
        shouldReconnect = false;
        if (client != null && client.isOpen()) {
            sendSystem(SYS_LEAVE);
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }


    private void connectAsync(int delaySeconds) {
        if (!shouldReconnect) {
            log.info("[{}] WebSocket connectAsync aborted: shouldReconnect=false", ZXCHelper.MOD_NAME);
            return;
        }
        if (connecting) {
            log.info("[{}] WebSocket connectAsync skipped: already connecting", ZXCHelper.MOD_NAME);
            return;
        }

        connecting = true;

        new Thread(() -> {
            try {
                if (delaySeconds > 0) {
                    log.info("[{}] Waiting {}s before reconnect...", ZXCHelper.MOD_NAME, delaySeconds);
                    Thread.sleep(delaySeconds * 1000L);
                }

                URI uri = new URI("wss://azerusclientserver-1.onrender.com");

                WebSocketClient ws = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        log.info("[{}] WebSocket connection established", ZXCHelper.MOD_NAME);
                        sendMcChat("Соединение с чатом установлено.");

                        sendSystem(SYS_JOIN);
                    }

                    @Override
                    public void onMessage(String message) {
                        handleIncoming(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        log.info("[{}] WebSocket closed: code={}, reason='{}', remote={}",
                                ZXCHelper.MOD_NAME, code, reason, remote);

                        sendMcChat("Соединение с чатом закрыто: " + reason);


                        if (remote) {
                            sendMcChat("Попытка переподключения через 5 секунд...");
                            scheduleReconnect(5);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        log.error("[{}] WebSocket error", ZXCHelper.MOD_NAME, ex);
                        sendMcChat("Ошибка WebSocket: " + ex.getClass().getSimpleName()
                                + ": " + ex.getMessage());
                        if (!isOpen()) {
                            scheduleReconnect(5);
                        }
                    }
                };

                ws.setConnectionLostTimeout(0);

                this.client = ws;
                log.info("[{}] Connecting to WebSocket {} ...", ZXCHelper.MOD_NAME, uri);
                ws.connectBlocking();

            } catch (Exception e) {
                log.error("[{}] Failed to connect to WebSocket", ZXCHelper.MOD_NAME, e);
                sendMcChat("Не удалось подключиться к чату: "
                        + e.getClass().getSimpleName() + ": " + e.getMessage());
                scheduleReconnect(10);
            } finally {
                connecting = false;
            }
        }, "ZXCHelper-WsConnect").start();
    }

    private void scheduleReconnect(int delaySeconds) {
        if (!shouldReconnect) {
            log.info("[{}] Reconnect not scheduled: shouldReconnect=false", ZXCHelper.MOD_NAME);
            return;
        }
        connectAsync(delaySeconds);
    }


    public void sendChat(String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String nick = mc.getSession().getUsername();
        String payload = nick + "|" + text;

        if (client != null && client.isOpen()) {
            client.send(payload);
        } else {
            sendMcChat("Нет соединения с внешним чатом.");
        }
    }


    private void sendSystem(String sysCode) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSession() == null) return;
        String nick = mc.getSession().getUsername();
        String payload = nick + "|" + sysCode;

        if (client != null && client.isOpen()) {
            client.send(payload);
        }
    }


    private void handleIncoming(String message) {
        String nick;
        String text;

        int sep = message.indexOf('|');
        if (sep >= 0) {
            nick = message.substring(0, sep);
            text = (sep + 1 < message.length()) ? message.substring(sep + 1) : "";
        } else {
            nick = "???";
            text = message;
        }

        final String finalNick = nick;
        final String finalText = text;

        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player == null) return;

            Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX)
                    .formatted(Formatting.GOLD);

            Text nickText = Text.literal(finalNick)
                    .formatted(Formatting.GRAY);

            if (SYS_JOIN.equals(finalText)) {
                Text status = Text.literal(" подключился")
                        .formatted(Formatting.GREEN);

                Text full = prefix.copy()
                        .append(nickText)
                        .append(status);

                mc.player.sendMessage(full);
                return;
            }

            if (SYS_LEAVE.equals(finalText)) {
                Text status = Text.literal(" отключился")
                        .formatted(Formatting.RED);

                Text full = prefix.copy()
                        .append(nickText)
                        .append(status);

                mc.player.sendMessage(full);
                return;
            }

            Text colon = Text.literal(": ");
            Text msgText = Text.literal(finalText).formatted(Formatting.YELLOW);


            Text full = prefix.copy()
                    .append(nickText)
                    .append(colon)
                    .append(msgText);

            mc.player.sendMessage(full);
        });
    }


    private void sendMcChat(String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player == null) return;
            String full = "%s%s".formatted(ZXCHelper.CHAT_PREFIX, text);
            mc.player.sendMessage(Text.literal(full));
        });
    }
}