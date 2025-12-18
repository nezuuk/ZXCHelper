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


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricEvents {


    public static void onClientTick(Consumer<MinecraftClient> listener) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                listener.accept(client);
            } catch (Throwable t) {
                log.error("[{}] Error in onClientTick listener", ZXCHelper.MOD_NAME, t);
            }
        });
    }


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


    public static void onChatReceived(BiConsumer<ChatChannel, Text> listener) {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            try {
                ChatChannel ch = overlay ? ChatChannel.HUD : ChatChannel.GAME;
                listener.accept(ch, message);
            } catch (Throwable t) {
                log.error("[{}] Error in onChatReceived(GAME)", ZXCHelper.MOD_NAME, t);
            }
        });

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


    public static void onChatSent(Consumer<String> listener) {
        ClientSendMessageEvents.CHAT.register((String message) -> {
            try {
                listener.accept(message);
            } catch (Throwable t) {
                log.error("[{}] Error in onChatSent", ZXCHelper.MOD_NAME, t);
            }
        });
    }

    public static void onCommandSent(Consumer<String> listener) {
        ClientSendMessageEvents.COMMAND.register((String command) -> {
            try {
                listener.accept(command);
            } catch (Throwable t) {
                log.error("[{}] Error in onCommandSent", ZXCHelper.MOD_NAME, t);
            }
        });
    }

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