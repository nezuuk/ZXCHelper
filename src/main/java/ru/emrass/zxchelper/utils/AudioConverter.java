package ru.emrass.zxchelper.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class AudioConverter {

    private static final Path SOURCE_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_sounds");
    private static final Path CACHE_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_sounds_cache");

    private static final String[] EXTENSIONS = {".mp4", ".mp3", ".wav", ".m4a", ".flac", ".ogg"};

    public static void process() {
        if (!Files.exists(SOURCE_DIR)) {
            try { Files.createDirectories(SOURCE_DIR); } catch (IOException e) {}
            return;
        }
        if (!Files.exists(CACHE_DIR)) {
            try { Files.createDirectories(CACHE_DIR); } catch (IOException e) {}
        }

        try (Stream<Path> paths = Files.walk(SOURCE_DIR)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String fileName = path.getFileName().toString().toLowerCase();
                for (String ext : EXTENSIONS) {
                    if (fileName.endsWith(ext)) {
                        convertFile(path, ext);
                        break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertFile(Path source, String ext) {
        String cleanName = source.getFileName().toString().replace(ext, "").toLowerCase().replaceAll("[^a-z0-9_]", "_");
        String oggName = cleanName + ".ogg";
        Path targetPath = CACHE_DIR.resolve(oggName);

        if (Files.exists(targetPath)) return;

        if (ext.equals(".ogg")) {
            try { Files.copy(source, targetPath); } catch (IOException e) {}
            return;
        }

        if (!ConverterDownloader.isReady()) {
            ZXCUtils.send("Конвертер не найден!", Formatting.RED);
            ConverterDownloader.checkAndDownload();
            return;
        }

        ZXCUtils.send("Обработка: " + source.getFileName(), Formatting.YELLOW);

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    ConverterDownloader.FFMPEG_PATH.toAbsolutePath().toString(),
                    "-i", source.toAbsolutePath().toString(),
                    "-vn",
                    "-acodec", "libvorbis",
                    "-y",
                    targetPath.toAbsolutePath().toString()
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder logOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                logOutput.append(line).append("\n");
            }

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);

            if (finished && process.exitValue() == 0) {
                ZXCUtils.send("Успешно: " + oggName, Formatting.GREEN);
            } else {
                System.err.println("=== FFMPEG ERROR LOG ===");
                System.err.println(logOutput.toString());
                System.err.println("========================");

                ZXCUtils.send("Ошибка FFmpeg! Чекни консоль.", Formatting.RED);
                try { Files.deleteIfExists(targetPath); } catch (IOException e) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
            ZXCUtils.send("Java Ошибка: " + e.getMessage(), Formatting.RED);
        }
    }
}