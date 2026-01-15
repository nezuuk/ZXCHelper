package ru.emrass.zxchelper.config.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.config.ZXCHelperConfig;


public class SoundTab {

    public static void build(ConfigBuilder builder, ZXCHelperConfig config, Identifier bg) {
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Звуки"));
        category.setBackground(bg);
        category.setCategoryBackground(bg);
        category.addEntry(entryBuilder.startStrList(Text.literal("Звуки в колесе"), config.getSoundWheelSounds())
                .setSaveConsumer(list -> {
                    config.setSoundWheelSounds(list);
                    ConfigManager.save();
                })
                .build());

    }
}