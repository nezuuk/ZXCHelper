package modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.config.ZXCHelperConfig;
import ru.emrass.zxchelper.pings.skins.SkinManager;

public class PingTab {

    public static void build(ConfigBuilder builder, ZXCHelperConfig config, Identifier bg) {
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Метки"));
        category.setBackground(bg);
        category.setCategoryBackground(bg);
        category.addEntry(entryBuilder.startColorField(Text.literal("Цвет метки"), config.getPingColor())
                .setDefaultValue(0x00FF00)
                .setTooltip(Text.literal("Выбор цвета метки"))
                .setSaveConsumer(color -> {
                    config.setPingColor(color);
                    ConfigManager.save();
                })
                .build());

        category.addEntry(entryBuilder.startDropdownMenu(
                        Text.literal("Скин Стрелки"),
                        config.getSelectedArrowSkin(),
                        name -> name)
                .setSelections(SkinManager.availableSkins)
                .setDefaultValue("")
                .setSaveConsumer(val -> {
                    config.setSelectedArrowSkin(val);
                    ConfigManager.save();
                })
                .build());
        category.addEntry(entryBuilder.startDropdownMenu(
                        Text.literal("Скин вражеской стрелки"),
                        config.getSelectedEnemySkin(),
                        name -> name)
                .setSelections(SkinManager.availableSkins)
                .setDefaultValue("")
                .setSaveConsumer(val -> {
                    config.setSelectedEnemySkin(val);
                    ConfigManager.save();
                })
                .build());
    }
}