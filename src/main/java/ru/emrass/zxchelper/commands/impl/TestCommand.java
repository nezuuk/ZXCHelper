package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.commands.BaseClientCommand;

import java.util.List;

public class TestCommand extends BaseClientCommand {

    public TestCommand() {
        super("test");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        src.getPlayer().sendMessage(Text.literal("Если эта команда появилась знчит обновление прошло успешно!"));
        return 1;
    }
}
