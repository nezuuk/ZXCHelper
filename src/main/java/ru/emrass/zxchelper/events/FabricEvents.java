package ru.emrass.zxchelper.events;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ZXCHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Утилита для удобной регистрации часто используемых Fabric-ивентов под 1.20.1.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricEvents {

    // ====== ТИК КЛИЕНТА ======

    public static void onClientTick(Consumer<MinecraftClient> listener) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                listener.accept(client);
            } catch (Throwable t) {
                log.error("[{}] Error in onClientTick listener", ZXCHelper.MOD_NAME, t);
            }
        });
    }

    // ====== ПОДКЛЮЧЕНИЕ / ОТКЛЮЧЕНИЕ ОТ СЕРВЕРА ======

    public static void onPlayJoin(BiConsumer<ClientPlayNetworkHandler, MinecraftClient> listener) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            try {
                listener.accept(handler, client);
            } catch (Throwable t) {
                log.error("[{}] Error in onPlayJoin listener", ZXCHelper.MOD_NAME, t);
            }
        });
    }

    public static void onPlayDisconnect(BiConsumer<ClientPlayNetworkHandler, MinecraftClient> listener) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            try {
                listener.accept(handler, client);
            } catch (Throwable t) {
                log.error("[{}] Error in onPlayDisconnect listener", ZXCHelper.MOD_NAME, t);
            }
        });
    }

    // ====== ПРИЁМ СООБЩЕНИЙ ЧАТА ОТ СЕРВЕРА ======

    /**
     * Все входящие сообщения от сервера:
     * - GAME(overlay=false)  -> GAME
     * - GAME(overlay=true)   -> HUD
     * - CHAT                 -> CHAT
     */
    public static void onChatReceived(BiConsumer<ChatChannel, Text> listener) {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            try {
                ChatChannel ch = overlay ? ChatChannel.HUD : ChatChannel.GAME;
                listener.accept(ch, message);
            } catch (Throwable t) {
                log.error("[{}] Error in onChatReceived(GAME)", ZXCHelper.MOD_NAME, t);
            }
        });

        // Чат игроков (подписанные сообщения)
        ClientReceiveMessageEvents.CHAT.register(
                (message, signedMessage, sender, params, receptionTimestamp) -> {
                    try {
                        listener.accept(ChatChannel.CHAT, message);
                    } catch (Throwable t) {
                        log.error("[{}] Error in onChatReceived(CHAT)", ZXCHelper.MOD_NAME, t);
                    }
                }
        );
    }

    // ====== ОТПРАВКА СООБЩЕНИЙ / КОМАНД НА СЕРВЕР ======

    /** Игрок отправил обычное чат-сообщение (не команду). */
    public static void onChatSent(Consumer<String> listener) {
        ClientSendMessageEvents.CHAT.register((String message) -> {
            try {
                listener.accept(message);
            } catch (Throwable t) {
                log.error("[{}] Error in onChatSent", ZXCHelper.MOD_NAME, t);
            }
        });
    }

    /** Игрок отправил команду (строка после '/'). */
    public static void onCommandSent(Consumer<String> listener) {
        ClientSendMessageEvents.COMMAND.register((String command) -> {
            try {
                listener.accept(command);
            } catch (Throwable t) {
                log.error("[{}] Error in onCommandSent", ZXCHelper.MOD_NAME, t);
            }
        });
    }

    // ====== HUD / ОВЕРЛЕЙ РЕНДЕР ======

    /**
     * Аналог RenderGameOverlayEvent: рисует поверх экрана.
     * DrawContext даёт MatrixStack и методы рисования текста/квадратов/иконок.
     */
    public static void onHudRender(BiConsumer<DrawContext, Float> listener) {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            try {
                listener.accept(drawContext, tickDelta);
            } catch (Throwable t) {
                log.error("[{}] Error in onHudRender", ZXCHelper.MOD_NAME, t);
            }
        });
    }
}