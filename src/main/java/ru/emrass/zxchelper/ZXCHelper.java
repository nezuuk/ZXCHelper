package ru.emrass.zxchelper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import ru.emrass.zxchelper.commands.CommandRegistry;
import ru.emrass.zxchelper.commands.impl.*;
import ru.emrass.zxchelper.commands.impl.admins.GenKeyCommand;
import ru.emrass.zxchelper.commands.impl.admins.RemoveSkinServer;
import ru.emrass.zxchelper.commands.impl.images.ZReloadCommand;
import ru.emrass.zxchelper.commands.impl.images.ZSyncImageCommand;
import ru.emrass.zxchelper.commands.impl.images.ZUploadSkins;
import ru.emrass.zxchelper.commands.impl.sounds.ZSoundCommand;
import ru.emrass.zxchelper.commands.impl.sounds.ZSyncCommand;
import ru.emrass.zxchelper.commands.impl.sounds.ZUploadCommand;
import ru.emrass.zxchelper.features.FeatureRegistry;
import ru.emrass.zxchelper.features.impl.*;
import ru.emrass.zxchelper.hwidcontrol.HWIDManager;
import ru.emrass.zxchelper.hwidcontrol.Role;
import ru.emrass.zxchelper.net.WsHandlerRegistry;
import ru.emrass.zxchelper.net.WsService;
import ru.emrass.zxchelper.net.manager.ErrorManager;
import ru.emrass.zxchelper.net.manager.SecretChatManager;
import ru.emrass.zxchelper.net.manager.StatusOnlineManager;
import ru.emrass.zxchelper.pings.manager.PingManager;
import ru.emrass.zxchelper.net.manager.sounds.SoundPlayManager;
import ru.emrass.zxchelper.net.manager.sounds.SoundSyncManager;
import ru.emrass.zxchelper.pings.render.PingRenderer;
import ru.emrass.zxchelper.pings.skins.SkinHandler;
import ru.emrass.zxchelper.pings.skins.SkinManager;

import ru.emrass.zxchelper.utils.ZXCPaths;
import ru.emrass.zxchelper.utils.sounds.AudioConverter;
import ru.emrass.zxchelper.utils.sounds.ConverterDownloader;
import ru.emrass.zxchelper.utils.sounds.SoundUtils;

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
    @Getter
    @Setter
    private Role playerRole = Role.USER;
    @Override
    public void onInitializeClient() {
        logger.info("init: {}", MOD_NAME);
        instance = this;
        ZXCPaths.init();
        webService.start();
        CommandRegistry.registerCommands(new SendCommand(), new ZHelpCommand(), new ZAddFriendCommand(),
                new ZRemoveFriendCommand(), new ZFriendsCommand(), new ZUpdateCommand(),
                new TestCommand(), new ZRollCommand(), new ZVersionCommand(), new ZOnlineCommand(),
                new ZUploadCommand(), new ZSyncCommand(), new ZSoundCommand(), new GenKeyCommand(),
                new ZReloadCommand(), new ZSyncImageCommand(), new ZUploadSkins(), new RemoveSkinServer());
        FeatureRegistry.registerFeatures(new AutoClicker(), glowHighlightFeature, new ChatWheelFeature(),
                new PingFeature(), new SoundWheelFeature());

        WsHandlerRegistry.registerHandlers(secretChatManager, new ErrorManager(), pingManager, new StatusOnlineManager(),
                new SoundSyncManager(), new SoundPlayManager(), new HWIDManager(), new SkinHandler());
        PingRenderer.register();

        ConverterDownloader.checkAndDownload();
        AudioConverter.process();
        SoundUtils.refreshSoundList();
        SkinManager.init();
    }


}
