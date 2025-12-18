package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.commands.CommandRegistry;

import java.util.List;


public class ZHelpCommand extends BaseClientCommand {

    public ZHelpCommand() {
        super("zhelp", "Показать все команды ZXCHelper", 0, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if (src.getPlayer() == null) return 0;

        Text header = Text.literal("✌ Список команд " + ZXCHelper.MOD_NAME + ":")
                .formatted(Formatting.GOLD);
        src.getPlayer().sendMessage(header);

        for (BaseClientCommand cmd : CommandRegistry.getCommands()) {
            Text line = Text.literal("✌ ")
                    .formatted(Formatting.GOLD)
                    .append(
                            Text.literal("/" + cmd.getName())
                                    .formatted(Formatting.GRAY)
                    )
                    .append(Text.literal(" - "))
                    .append(
                            Text.literal(cmd.getDescription())
                                    .formatted(Formatting.WHITE)
                    );

            src.getPlayer().sendMessage(line);
        }

        return 1;
    }
}