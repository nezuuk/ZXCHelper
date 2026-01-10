package ru.emrass.zxchelper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.emrass.zxchelper.commands.CommandRegistry;
import ru.emrass.zxchelper.commands.impl.*;
import ru.emrass.zxchelper.features.FeatureRegistry;
import ru.emrass.zxchelper.features.impl.AutoClicker;
import ru.emrass.zxchelper.features.impl.GlowHighlightFeature;
import ru.emrass.zxchelper.gui.ChatWheelScreen;
import ru.emrass.zxchelper.net.WsHandlerRegistry;
import ru.emrass.zxchelper.net.WsService;
import ru.emrass.zxchelper.net.manager.ErrorManager;
import ru.emrass.zxchelper.net.manager.SecretChatManager;

@Slf4j
public class ZXCHelper implements ClientModInitializer {
    public static final String MOD_NAME = "ZXCHelper";
    public static final String CHAT_PREFIX = "[✌] ";
    @Getter
    private final WsService webService = new WsService();
    @Getter
    public static ZXCHelper instance;
    @Getter
    private final GlowHighlightFeature glowHighlightFeature = new GlowHighlightFeature();
    @Getter
    private final SecretChatManager secretChatManager = new SecretChatManager(webService);
    private static KeyBinding chatWheelKey;

    @Override
    public void onInitializeClient() {
        log.info("init: {}", MOD_NAME);
        instance = this;
        webService.start();
        CommandRegistry.registerCommands(new SendCommand(), new ZHelpCommand(), new ZAddFriendCommand(),
                new ZRemoveFriendCommand(), new ZFriendsCommand(), new ZUpdateCommand(),
                new TestCommand(), new ZRollCommand());
        FeatureRegistry.registerFeatures(new AutoClicker(), glowHighlightFeature);
        WsHandlerRegistry.registerHandlers(secretChatManager, new ErrorManager());

        chatWheelKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zxchelper.wheel", // Ключ перевода
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,       // Кнопка V
                "category.zxchelper"   // Категория в настройках
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Если кнопка нажата, игрока нет в другом меню и мир загружен
            if (chatWheelKey.isPressed() && client.currentScreen == null && client.player != null) {
                // Получаем код клавиши, чтобы знать, когда её отпустят
                int keyCode = KeyBindingHelper.getBoundKeyOf(chatWheelKey).getCode();
                client.setScreen(new ChatWheelScreen(keyCode));
            }
        });
    }


}
