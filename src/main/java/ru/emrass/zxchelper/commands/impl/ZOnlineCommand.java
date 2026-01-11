package ru.emrass.zxchelper.commands.impl;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.net.WsMessageType;

import java.util.List;

public class ZOnlineCommand extends BaseClientCommand {
    public ZOnlineCommand() {
        super("zonline", "получить список подключенных юзеров");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.ONLINE, new JsonObject());

        return 0;
    }
}
