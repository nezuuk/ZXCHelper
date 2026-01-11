package ru.emrass.zxchelper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import ru.emrass.zxchelper.commands.CommandRegistry;
import ru.emrass.zxchelper.commands.impl.*;
import ru.emrass.zxchelper.commands.impl.sounds.ZSoundCommand;
import ru.emrass.zxchelper.commands.impl.sounds.ZSyncCommand;
import ru.emrass.zxchelper.commands.impl.sounds.ZUploadCommand;
import ru.emrass.zxchelper.features.FeatureRegistry;
import ru.emrass.zxchelper.features.impl.AutoClicker;
import ru.emrass.zxchelper.features.impl.ChatWheelFeature;
import ru.emrass.zxchelper.features.impl.GlowHighlightFeature;
import ru.emrass.zxchelper.features.impl.PingFeature;
import ru.emrass.zxchelper.net.WsHandlerRegistry;
import ru.emrass.zxchelper.net.WsService;
import ru.emrass.zxchelper.net.manager.ErrorManager;
import ru.emrass.zxchelper.net.manager.SecretChatManager;
import ru.emrass.zxchelper.net.manager.StatusOnlineManager;
import ru.emrass.zxchelper.net.manager.pings.PingManager;
import ru.emrass.zxchelper.net.manager.sounds.SoundPlayManager;
import ru.emrass.zxchelper.net.manager.sounds.SoundSyncManager;
import ru.emrass.zxchelper.render.PingRenderer;
import ru.emrass.zxchelper.utils.SoundPackGenerator;

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
    @Getter
    private final PingManager pingManager = new PingManager();

    @Override
    public void onInitializeClient() {
        log.info("init: {}", MOD_NAME);
        instance = this;
        webService.start();
        CommandRegistry.registerCommands(new SendCommand(), new ZHelpCommand(), new ZAddFriendCommand(),
                new ZRemoveFriendCommand(), new ZFriendsCommand(), new ZUpdateCommand(),
                new TestCommand(), new ZRollCommand(), new ZVersionCommand(), new ZOnlineCommand(),
                new ZUploadCommand(), new ZSyncCommand(), new ZSoundCommand());
        FeatureRegistry.registerFeatures(new AutoClicker(), glowHighlightFeature, new ChatWheelFeature(),
                new PingFeature());

        WsHandlerRegistry.registerHandlers(secretChatManager, new ErrorManager(), pingManager, new StatusOnlineManager(),
                new SoundSyncManager(), new SoundPlayManager());
        PingRenderer.register();


        SoundPackGenerator.init();


        SoundPackGenerator.generateFilesOnStartup();


        ClientLifecycleEvents.CLIENT_STARTED.register(SoundPackGenerator::checkAutoEnable);
    }


}
