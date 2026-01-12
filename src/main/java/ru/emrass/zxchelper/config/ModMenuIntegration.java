package ru.emrass.zxchelper.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.utils.SoundUtils;

import java.util.List;
import java.util.Optional;

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

            general.addEntry(entryBuilder.startStrList(Text.literal("Звуки Колеса"), config.getSoundWheelSounds())
                    .setDefaultValue(List.of())
                    .setTooltip(Text.literal("Впиши сюда названия файлов (без .ogg и т.д), которые должны быть в колесе."))
                    .setErrorSupplier(list -> {
                        if (list.size() > 8) {
                            return Optional.of(Text.literal("Слишком много звуков! Максимум 8."));
                        }
                        for (String soundName : list) {
                            if (!SoundUtils.loadedSoundNames.contains(soundName)) {
                                return Optional.of(Text.literal("Ошибка: Звук '" + soundName + "' не найден в папке zxc_sounds_cache!"));
                            }
                        }

                        return Optional.empty();
                    })
                    .setSaveConsumer(list -> {
                        if (list.size() > 8) list = list.subList(0, 8);
                        config.setSoundWheelSounds(list);
                        ConfigManager.save();
                    })
                    .build());

            general.addEntry(entryBuilder.startTextDescription(Text.literal("§eНастройки Пингов")).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Цвет метки"), config.getPingColor())
                    .setDefaultValue(0x00FF00)
                    .setTooltip(Text.literal("Выбор цвета метки"))
                    .setSaveConsumer(color -> {
                        config.setPingColor(color);
                        ConfigManager.save();
                    })
                    .build());
            general.addEntry(entryBuilder.startTextDescription(Text.literal("§eКлюч доступа")).build());
            general.addEntry(entryBuilder.startStrField(Text.literal("Ключ доступа"), config.getLicenseKey())
                    .setDefaultValue("")
                    .setTooltip(Text.literal("Вставь сюда ключ"))
                    .setSaveConsumer(newValue -> {
                        config.setLicenseKey(newValue);
                        ConfigManager.save();
                    })
                    .build());
            general.addEntry(entryBuilder.startTextDescription(Text.literal("§eОстальное")).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Статус функций"), config.isShowStatus())
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Показывать статус функций?"))
                    .setSaveConsumer(newValue -> {
                        config.setShowStatus(newValue);
                        ConfigManager.save();
                    })
                    .build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Префикс чата"), config.isActivePrefixChat())
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Включить префикс чата? # and №"))
                    .setSaveConsumer(newValue -> {
                        config.setActivePrefixChat(newValue);
                        ConfigManager.save();
                    })
                    .build());
            general.addEntry(entryBuilder.startTextDescription(Text.literal("§eПодсветка")).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Префикс чата"), config.isActivePrefixChat())
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Включить префикс чата? # and №"))
                    .setSaveConsumer(newValue -> {
                        config.setActivePrefixChat(newValue);
                        ConfigManager.save();
                    })
                    .build());

            return builder.build();
        };
    }
}