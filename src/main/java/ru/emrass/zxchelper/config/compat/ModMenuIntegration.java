package ru.emrass.zxchelper.config.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("ZXC Helper Config"));

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // --- Настройка фраз колеса ---
            general.addEntry(entryBuilder.startStrList(Text.literal("Фразы колеса"), ConfigManager.getConfig().getChatWheelMessages())
                    .setDefaultValue(List.of("SS", "Help me!", "Go Roshan"))
                    .setTooltip(Text.literal("Список сообщений для быстрого чата"))
                    .setSaveConsumer(newMessages -> {
                        ConfigManager.getConfig().setChatWheelMessages(newMessages);
                        ConfigManager.save();
                    })
                    .build());

            return builder.build();
        };
    }
}