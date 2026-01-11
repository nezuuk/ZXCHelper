package ru.emrass.zxchelper.net.manager.sounds;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.SoundPackGenerator;

public class SoundPlayManager extends BaseWsHandler {
    public SoundPlayManager() {
        super(WsMessageType.PLAY_SOUND);
    }

    @Override
    public void handle(JsonObject json) {
        String soundId = json.get("id").getAsString();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        //Если вернуть в радиусе, то подстваить x,y,z c json
        MinecraftClient.getInstance().execute(() -> {
            if (SoundPackGenerator.loadedSoundNames.contains(soundId)) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if(player == null) return;
                Identifier id = new Identifier("zxchelper", soundId);
                SoundEvent soundEvent = SoundEvent.of(id);

                if (MinecraftClient.getInstance().world != null) {
                    MinecraftClient.getInstance().world.playSound(
                            player.getX(), player.getY(), player.getZ(),
                            soundEvent,
                            SoundCategory.PLAYERS,
                            1.0f,
                            1.0f,
                            false
                    );
                }

            } else {
                if (MinecraftClient.getInstance().player != null) {
                    Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
                    Text sound = Text.literal(soundId).formatted(Formatting.GRAY);
                    Text download = Text.literal("СКАЧАТЬ").formatted(Formatting.GREEN)
                            .styled(style -> style
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zxc sync"))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Нажми, чтобы синхронизировать звуки")))
                            );
                    Text errorMsg = prefix.copy().append("Звук %s не найден!".formatted(sound)).formatted(Formatting.RED)
                            .append(download);

                    MinecraftClient.getInstance().player.sendMessage(errorMsg, false);
                }
            }
        });
    }
}
