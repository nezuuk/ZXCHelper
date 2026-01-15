package ru.emrass.zxchelper.commands.impl.sounds;


import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;

import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.sounds.SoundUtils;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.util.List;

public class ZSoundCommand extends BaseClientCommand {
    public ZSoundCommand() {
        super("zsound", "Воспроизвести звук всем", 1, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        String soundId = args.get(0);
        MinecraftClient client = MinecraftClient.getInstance();

        if (!SoundUtils.loadedSoundNames.contains(soundId)) {
            ZXCUtils.send("Ошибка: Звук '" + soundId + "' не найден!", Formatting.RED);
            return 0;
        }

        if (client.player != null) {
            Vec3d pos = client.player.getPos();

            JsonObject json = new JsonObject();
            json.addProperty("id", soundId);
            json.addProperty("x", pos.x);
            json.addProperty("y", pos.y);
            json.addProperty("z", pos.z);

            ZXCHelper.getInstance().getWebService().send(WsMessageType.PLAY_SOUND,json);

            ZXCUtils.send("Воиспроизводится: %s.ogg".formatted(soundId), Formatting.GRAY);
        }
        return 1;
    }

    @Override
    protected List<String> complete(FabricClientCommandSource src, List<String> argsSoFar) {
        return SoundUtils.loadedSoundNames;
    }
}
