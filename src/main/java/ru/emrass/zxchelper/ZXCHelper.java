package ru.emrass.zxchelper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import ru.emrass.zxchelper.commands.CommandRegistry;
import ru.emrass.zxchelper.commands.impl.*;
import ru.emrass.zxchelper.commands.impl.admins.GenKeyCommand;
import ru.emrass.zxchelper.commands.impl.admins.GetHWID;
import ru.emrass.zxchelper.commands.impl.sounds.ZSoundCommand;
import ru.emrass.zxchelper.commands.impl.sounds.ZSyncCommand;
import ru.emrass.zxchelper.commands.impl.sounds.ZUploadCommand;
import ru.emrass.zxchelper.features.FeatureRegistry;
import ru.emrass.zxchelper.features.impl.AutoClicker;
import ru.emrass.zxchelper.features.impl.ChatWheelFeature;
import ru.emrass.zxchelper.features.impl.GlowHighlightFeature;
import ru.emrass.zxchelper.features.impl.PingFeature;
import ru.emrass.zxchelper.hwidcontrol.HWIDManager;
import ru.emrass.zxchelper.net.WsHandlerRegistry;
import ru.emrass.zxchelper.net.WsService;
import ru.emrass.zxchelper.net.manager.ErrorManager;
import ru.emrass.zxchelper.net.manager.SecretChatManager;
import ru.emrass.zxchelper.net.manager.StatusOnlineManager;
import ru.emrass.zxchelper.net.manager.pings.PingManager;
import ru.emrass.zxchelper.net.manager.sounds.SoundPlayManager;
import ru.emrass.zxchelper.net.manager.sounds.SoundSyncManager;
import ru.emrass.zxchelper.render.PingRenderer;
import ru.emrass.zxchelper.utils.AudioConverter;
import ru.emrass.zxchelper.utils.ConverterDownloader;
import ru.emrass.zxchelper.utils.SoundUtils;

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
    @Getter
    private Logger logger = log;
    @Override
    public void onInitializeClient() {
        logger.info("init: {}", MOD_NAME);
        instance = this;
        webService.start();
        CommandRegistry.registerCommands(new SendCommand(), new ZHelpCommand(), new ZAddFriendCommand(),
                new ZRemoveFriendCommand(), new ZFriendsCommand(), new ZUpdateCommand(),
                new TestCommand(), new ZRollCommand(), new ZVersionCommand(), new ZOnlineCommand(),
                new ZUploadCommand(), new ZSyncCommand(), new ZSoundCommand(), new GenKeyCommand());
        FeatureRegistry.registerFeatures(new AutoClicker(), glowHighlightFeature, new ChatWheelFeature(),
                new PingFeature());

        WsHandlerRegistry.registerHandlers(secretChatManager, new ErrorManager(), pingManager, new StatusOnlineManager(),
                new SoundSyncManager(), new SoundPlayManager(), new HWIDManager());
        PingRenderer.register();


        ConverterDownloader.checkAndDownload();
        AudioConverter.process();
        SoundUtils.refreshSoundList();
    }


}
