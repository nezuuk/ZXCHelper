package ru.emrass.zxchelper.commands.impl.admins;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;

public class GenKeyCommand extends BaseClientCommand {

    public GenKeyCommand() {
        super("zgenkey", "Сгенерировать ключ",1, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        JsonObject json = new JsonObject();
        boolean isAdmin = Boolean.parseBoolean(args.get(0));
        json.addProperty("admin", isAdmin);
        ZXCUtils.send("Генерируем ключ...");
        ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.GENERATE_KEY, json);
        return 1;
    }
}