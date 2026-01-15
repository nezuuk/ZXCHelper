package ru.emrass.zxchelper.commands.impl.images;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.pings.skins.SkinHandler;
import ru.emrass.zxchelper.pings.skins.SkinManager;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZSyncImageCommand extends BaseClientCommand {
    public ZSyncImageCommand() {
        super("zsyncimage", "Загрузить новые звуки с сервера");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        CompletableFuture.runAsync(SkinHandler::requestSync);

        return 1;
    }
}
