package ru.emrass.zxchelper.config.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.config.ZXCHelperConfig;

import java.util.List;
import java.util.Optional;

public class GeneralTab {

    public static void build(ConfigBuilder builder, ZXCHelperConfig config, Identifier bg) {
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Общее"));
        category.setBackground(bg);
        category.setCategoryBackground(bg);
        category.addEntry(entryBuilder.startStrList(Text.literal("Колесо чата"), config.getChatWheelMessages())
                .setDefaultValue(List.of("pew pew pew"))
                .setErrorSupplier(list -> {
                    if (list.size() > 8) {
                        return Optional.of(Text.literal("Слишком много фраз! Максимум 8."));
                    }
                    return Optional.empty();
                })
                .setSaveConsumer(list -> {
                    if (list.size() > 8) list = list.subList(0, 8);
                    config.setChatWheelMessages(list);
                    ConfigManager.save();
                })
                .build());

        category.addEntry(entryBuilder.startStrField(Text.literal("Ключ доступа"), config.getLicenseKey())
                .setDefaultValue("")
                .setTooltip(Text.literal("Вставь сюда ключ"))
                .setSaveConsumer(newValue -> {
                    config.setLicenseKey(newValue);
                    ConfigManager.save();
                })
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Статус функций"), config.isShowStatus())
                .setDefaultValue(false)
                .setTooltip(Text.literal("Показывать статус функций?"))
                .setSaveConsumer(newValue -> {
                    config.setShowStatus(newValue);
                    ConfigManager.save();
                })
                .build());
        category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Префикс чата"), config.isActivePrefixChat())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Включить префикс чата? # and №"))
                .setSaveConsumer(newValue -> {
                    config.setActivePrefixChat(newValue);
                    ConfigManager.save();
                })
                .build());
    }
}