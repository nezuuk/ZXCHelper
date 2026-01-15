package ru.emrass.zxchelper.commands.impl.images;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.pings.skins.SkinHandler;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;

public class ZUploadSkins extends BaseClientCommand {
    public ZUploadSkins() {
        super("zuploadskins", "Загрузить все скины на сервер");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        SkinHandler.uploadAll();
        ZXCUtils.send("Все имеющиеся картинки отправлены!");
        return 1;

    }
}
