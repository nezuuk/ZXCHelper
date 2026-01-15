package ru.emrass.zxchelper.commands.impl.images;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.pings.skins.SkinManager;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;

public class ZReloadCommand extends BaseClientCommand {

    public ZReloadCommand() {
        super("zreload", "Перезагрузить картинки");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        SkinManager.loadLocalSkins();
        ZXCUtils.send("Картинки перезагруженны!");
        return 1;
    }


}