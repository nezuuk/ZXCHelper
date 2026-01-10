package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;

import java.util.List;
import java.util.Random;

public class ZRollCommand extends BaseClientCommand {
    public ZRollCommand() {
        super("zroll", "roll 0-100 без подкруток");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        int roll = new Random().nextInt(100);
        ZXCHelper.getInstance().getSecretChatManager().sendChat("выбросил %s!".formatted(roll));
        return 0;
    }
}
