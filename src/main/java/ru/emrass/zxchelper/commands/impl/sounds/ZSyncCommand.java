package ru.emrass.zxchelper.commands.impl.sounds;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.net.manager.sounds.SoundSyncManager;
import ru.emrass.zxchelper.utils.AudioConverter;
import ru.emrass.zxchelper.utils.SoundUtils;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZSyncCommand extends BaseClientCommand {
    public ZSyncCommand() {
        super("zsync", "Загрузить новые звуки с сервера");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        CompletableFuture.runAsync(() -> {
            AudioConverter.process();
            SoundUtils.refreshSoundList();
            SoundSyncManager.requestSync();
            ZXCUtils.send("Звуки обновлены! Доступно: " + SoundUtils.loadedSoundNames.size(), Formatting.GREEN);
        });

        return 1;
    }
}
