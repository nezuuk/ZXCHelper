package ru.emrass.zxchelper.commands.impl.admins;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.hwidcontrol.Role;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.pings.skins.SkinManager;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;

public class RemoveSkinServer extends BaseClientCommand {

    public RemoveSkinServer() {
        super("zremoveskin", "Удалить скин",1,false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if(!ZXCHelper.getInstance().getPlayerRole().hasPermission(Role.ADMIN)){
            ZXCUtils.send("Отказано...");
            return 0;
        }
        String skin = args.get(0);
        JsonObject json = new JsonObject();
        json.addProperty("skin", skin);
        ZXCHelper.getInstance().getWebService().send(WsMessageType.REMOVE_SKIN,json);
        SkinManager.deleteSkin(skin);
        return 1;
    }

    @Override
    protected List<String> complete(FabricClientCommandSource src, List<String> argsSoFar) {
        return SkinManager.availableSkins;
    }
}