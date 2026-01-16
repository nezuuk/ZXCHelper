package ru.emrass.zxchelper.ui.menu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.config.ZXCHelperConfig;
import ru.emrass.zxchelper.hwidcontrol.Role;
import ru.emrass.zxchelper.pings.skins.SkinManager;
import ru.emrass.zxchelper.ui.lib.ZxcBuilder;
import ru.emrass.zxchelper.ui.lib.ZxcEntryBuilder;
import ru.emrass.zxchelper.utils.sounds.SoundUtils;

import java.util.List;
import java.util.Optional;

public class ConfigMenu {

    public static Screen create(Screen parent) {
        ZXCHelperConfig config = ConfigManager.getConfig();
        ZxcBuilder builder = new ZxcBuilder(parent);

        ZxcEntryBuilder entryBuilder = ZxcEntryBuilder.create();

        var general = builder.category("ОБЩЕЕ");

        general.addEntry(entryBuilder.startStrList(Text.literal("Колесо чата"), config.getChatWheelMessages())
                .setDefaultValue(List.of(""))
                .setErrorSupplier(list -> {
                    if (list.size() > 8) {
                        return Optional.of(Text.literal("Слишком много фраз!"));
                    }
                    return Optional.empty();
                })
                .setSaveConsumer(list -> {
                    if (list.size() > 8) list = list.subList(0, 8);
                    config.setChatWheelMessages(list);
                    ConfigManager.save();
                })
                .build());


        general.addEntry(entryBuilder.startStringField(Text.literal("Ключ доступа"), config.getLicenseKey())
                .setDefaultValue("")
                .setTooltip(Text.literal("Вставь сюда ключ"))
                .setErrorSupplier(s -> {
                    if (!s.isEmpty() && s.length() < 5) return Optional.of(Text.literal("Недопустимый формат ключа"));
                    return Optional.empty();
                })
                .setSaveConsumer(val -> {
                    config.setLicenseKey(val);
                    ConfigManager.save();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Статус функций"), config.isShowStatus())
                .setSaveConsumer(val -> {
                    config.setShowStatus(val);
                    ConfigManager.save();
                })
                .build());

        var pings = builder.category("МЕТКИ");
        pings.addEntry(entryBuilder.startDropdownMenu(Text.literal("Скин Стрелки"), config.getSelectedArrowSkin(), SkinManager.availableSkins)
                .setSaveConsumer(val -> {
                    config.setSelectedArrowSkin(val);
                    ConfigManager.save();
                })
                .build());

        pings.addEntry(entryBuilder.startDropdownMenu(Text.literal("Скин Вражеской Стрелки"), config.getSelectedEnemySkin(), SkinManager.availableSkins)
                .setSaveConsumer(val -> {
                    config.setSelectedEnemySkin(val);
                    ConfigManager.save();
                })
                .build());

        pings.addEntry(entryBuilder.startColorPicker(Text.literal("Цвет метки"), config.getPingColor())
                .setSaveConsumer(color -> {
                    config.setPingColor(color);
                    ConfigManager.save();
                })
                .build());

        var sounds = builder.category("ЗВУКИ");
        sounds.addEntry(entryBuilder.startStrList(Text.literal("Колесо звуов"), config.getSoundWheelSounds())
                .setDefaultValue(List.of(""))
                .setErrorSupplier(list -> {
                    for (String soundName : list) {
                        if (!SoundUtils.loadedSoundNames.contains(soundName)) {
                            return Optional.of(Text.literal("Ошибка: Звук '" + soundName + "' не найден!"));
                        }
                    }
                    if (list.size() > 8) {
                        return Optional.of(Text.literal("Слишком много звуков!"));
                    }
                    return Optional.empty();
                })
                .setSaveConsumer(list -> {
                    if (list.size() > 8) list = list.subList(0, 8);
                    config.setSoundWheelSounds(list);
                    ConfigManager.save();
                })
                .build());

        if(ZXCHelper.getInstance().getPlayerRole().hasPermission(Role.OWNER)){
            var test = builder.category("TEST");
            test.addEntry(entryBuilder.startStringField(Text.literal("Test"), ConfigManager.getConfig().getSelectedArrowSkin())
                            .setSuggestions(SkinManager.availableSkins)
                    .setDefaultValue("")
                    .setTooltip(Text.literal("Вставь сюда меня...."))
                    .setErrorSupplier(s -> {
                        if (!s.isEmpty() && s.length() < 3) return Optional.of(Text.literal("Недопустимый формат ключа"));
                        return Optional.empty();
                    })
                    .setSaveConsumer(val -> {

                    })
                    .build());
        }
        return builder.build();
    }
}