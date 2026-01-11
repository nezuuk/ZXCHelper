package ru.emrass.zxchelper.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ZXCHelperConfig config = ConfigManager.getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("ZXC Helper Config"));

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Основные"));
            general.setBackground(new Identifier("zxchelper", "textures/config_bg.png"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startStrList(Text.literal("Колесо чата"), config.getChatWheelMessages())
                    .setDefaultValue(List.of("SS", "Help me!", "Go Roshan", "Push mid", "Back!", "GG WP"))
                    .setSaveConsumer(list -> {
                        if (list.size() > 8) list = list.subList(0, 8);
                        config.setChatWheelMessages(list);
                        ConfigManager.save();
                    })
                    .build());


            general.addEntry(entryBuilder.startTextDescription(Text.literal("§eНастройки Пингов")).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Цвет метки (Местность)"), config.getPingColor())
                    .setDefaultValue(0x00FF00) // Зеленый
                    .setTooltip(Text.literal("Нажми на цветной квадрат для выбора цвета"))
                    .setSaveConsumer(color -> {
                        config.setPingColor(color);
                        ConfigManager.save();
                    })
                    .build());

            return builder.build();
        };
    }
}