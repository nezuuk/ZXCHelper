package ru.emrass.zxchelper.commands.impl.admins;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.hwidcontrol.HWIDManager;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;

public class GetHWID extends BaseClientCommand {

    public GetHWID() {
        super("getHWID", "Узнать HWID");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        ZXCUtils.send(HWIDManager.getHWID());
        return 1;
    }
}