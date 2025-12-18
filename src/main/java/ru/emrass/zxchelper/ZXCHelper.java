package ru.emrass.zxchelper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ClientModInitializer;
import ru.emrass.zxchelper.commands.CommandRegistry;
import ru.emrass.zxchelper.commands.impl.*;
import ru.emrass.zxchelper.features.FeatureRegistry;
import ru.emrass.zxchelper.features.impl.AutoClicker;
import ru.emrass.zxchelper.features.impl.GlowHighlightFeature;

import java.util.Random;

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

    @Override
    public void onInitializeClient() {
        log.info("init: {}", MOD_NAME);
        instance = this;
        webService.start();
        new Random();
        CommandRegistry.registerCommands(new SendCommand(), new ZHelpCommand(), new ZAddFriendCommand(), new ZRemoveFriendCommand(), new ZFriendsCommand(), new ZUpdateCommand(), new TestCommand());
        FeatureRegistry.registerFeatures(new AutoClicker(), glowHighlightFeature);


    }


}
