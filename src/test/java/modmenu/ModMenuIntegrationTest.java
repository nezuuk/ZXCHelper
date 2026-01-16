package modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.config.ZXCHelperConfig;

public class ModMenuIntegrationTest implements ModMenuApi {
    private Identifier bg = new Identifier("zxchelper", "textures/config_bg.png");

//    @Override
//    public ConfigScreenFactory<?> getModConfigScreenFactory() {
//        return ConfigMenu::create;
//    }
//
//}

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ZXCHelperConfig config = ConfigManager.getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("ZXC Helper Settings"));

            GeneralTab.build(builder, config, bg);
            PingTab.build(builder, config, bg);
            SoundTab.build(builder, config, bg);
            AdminTab.build(builder, config, bg);
            return builder.build();
        };

    }
}