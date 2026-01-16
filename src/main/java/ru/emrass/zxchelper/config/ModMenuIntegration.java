package ru.emrass.zxchelper.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.ui.menu.ConfigMenu;

public class ModMenuIntegration implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigMenu::create;
    }

}

//    @Override
//    public ConfigScreenFactory<?> getModConfigScreenFactory() {
//        return parent -> {
//            ZXCHelperConfig config = ConfigManager.getConfig();
//
//            ConfigBuilder builder = ConfigBuilder.create()
//                    .setParentScreen(parent)
//                    .setTitle(Text.literal("ZXC Helper Settings"));
//
//            GeneralTab.build(builder, config, bg);
//            PingTab.build(builder, config, bg);
//            SoundTab.build(builder, config, bg);
//            AdminTab.build(builder, config, bg);
//            return builder.build();
//        };
//
//    }
//}