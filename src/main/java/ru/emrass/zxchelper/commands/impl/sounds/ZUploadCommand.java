package ru.emrass.zxchelper.commands.impl.sounds;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.net.manager.sounds.SoundSyncManager;
import ru.emrass.zxchelper.utils.SoundUtils;

import java.util.List;

public class ZUploadCommand extends BaseClientCommand {
    public ZUploadCommand() {
        super("zupload", "Загрузить свои звуки на сервер", 1, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        String fileName = args.get(0);
        SoundSyncManager.uploadSound(fileName);
        return 1;
    }

    @Override
    protected List<String> complete(FabricClientCommandSource src, List<String> argsSoFar) {
        return SoundUtils.loadedSoundNames;
    }
}
