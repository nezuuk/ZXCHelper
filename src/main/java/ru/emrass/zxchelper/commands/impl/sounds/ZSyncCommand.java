package ru.emrass.zxchelper.commands.impl.sounds;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.net.manager.sounds.SoundSyncManager;

import java.util.List;

public class ZSyncCommand extends BaseClientCommand {
    public ZSyncCommand() {
        super("zsync", "Загрузить новые звуки с сервер");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        SoundSyncManager.requestSync();
        return 1;
    }
}
