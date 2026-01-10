package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;

import java.util.List;

public class SendCommand extends BaseClientCommand {

    public SendCommand() {
        super("send", "Отправить сообщение", 1, true);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        String msg = args.isEmpty() ? "" : args.get(0);
        ZXCHelper.getInstance().getSecretChatManager().sendChat(msg);
        return 1;
    }
}