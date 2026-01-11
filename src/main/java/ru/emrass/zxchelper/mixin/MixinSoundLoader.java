package ru.emrass.zxchelper.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.RepeatingAudioStream;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Mixin(SoundLoader.class)
public class MixinSoundLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger("ZXC-LOADER");
    private static final Path CACHE_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_sounds_cache");

    @Inject(
            method = "loadStreamed(Lnet/minecraft/util/Identifier;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void loadExternalStream(Identifier id, boolean repeatInstantly, CallbackInfoReturnable<CompletableFuture<AudioStream>> cir) {
        if (id.getNamespace().equals("zxchelper") && id.getPath().contains("external/")) {

            LOGGER.info("[ZXC] ПЕРЕХВАЧЕН ЗАПРОС: {}", id);

            String path = id.getPath();
            String fileName = path.substring(path.lastIndexOf("external/") + "external/".length());

            if (!fileName.endsWith(".ogg")) fileName += ".ogg";

            Path filePath = CACHE_DIR.resolve(fileName);

            if (!Files.exists(filePath)) {
                return;
            }

            CompletableFuture<AudioStream> future = CompletableFuture.supplyAsync(() -> {
                try {
                    InputStream inputStream = new FileInputStream(filePath.toFile());
                    return repeatInstantly
                            ? new RepeatingAudioStream(OggAudioStream::new, inputStream)
                            : new OggAudioStream(inputStream);
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, Util.getMainWorkerExecutor());

            cir.setReturnValue(future);
        }
    }
}