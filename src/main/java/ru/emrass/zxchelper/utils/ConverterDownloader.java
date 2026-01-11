package ru.emrass.zxchelper.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Formatting;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ConverterDownloader {

    public static final Path TOOLS_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_tools");
    public static final Path FFMPEG_PATH = TOOLS_DIR.resolve("ffmpeg.exe");


    private static final String DOWNLOAD_URL = "https://github.com/eugeneware/ffmpeg-static/releases/download/b4.4/win32-x64";

    public static boolean isReady() {
        return Files.exists(FFMPEG_PATH);
    }

    public static void checkAndDownload() {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) return;

        if (isReady()) return;

        CompletableFuture.runAsync(() -> {
            try {
                ZXCUtils.send("Первый запуск: скачиваю конвертер (это займет пару секунд)...", Formatting.YELLOW);

                if (!Files.exists(TOOLS_DIR)) Files.createDirectories(TOOLS_DIR);

                try (BufferedInputStream in = new BufferedInputStream(new URL(DOWNLOAD_URL).openStream());
                     FileOutputStream out = new FileOutputStream(FFMPEG_PATH.toFile())) {

                    byte[] data = new byte[1024];
                    int count;
                    while ((count = in.read(data, 0, 1024)) != -1) {
                        out.write(data, 0, count);
                    }
                }

                ZXCUtils.send("Конвертер скачан! Теперь звуки будут работать.", Formatting.GREEN);

            } catch (IOException e) {
                e.printStackTrace();
                ZXCUtils.send("Ошибка скачивания конвертера: " + e.getMessage(), Formatting.RED);
            }
        });
    }
}