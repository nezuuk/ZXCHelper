package ru.emrass.zxchelper.utils.sounds;

import net.fabricmc.loader.api.FabricLoader;
import ru.emrass.zxchelper.utils.ZXCPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SoundUtils {


    public static List<String> loadedSoundNames = new ArrayList<>();


    public static void refreshSoundList() {
        loadedSoundNames.clear();

        try (Stream<Path> paths = Files.walk(ZXCPaths.SCACHE)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".ogg"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        String soundId = fileName.replace(".ogg", "");

                        loadedSoundNames.add(soundId);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[ZXC] Список звуков обновлен: " + loadedSoundNames.size() + " файлов.");
    }
}