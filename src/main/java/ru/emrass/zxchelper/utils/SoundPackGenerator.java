package ru.emrass.zxchelper.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.util.Formatting;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SoundPackGenerator {

    private static final String PACK_NAME = "ZXC_Generated_Sounds";
    private static final Path SOURCE_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_sounds");
    private static final Path PACK_DIR = FabricLoader.getInstance().getGameDir().resolve("resourcepacks/" + PACK_NAME);

    public static List<String> loadedSoundNames = new ArrayList<>();

    public static void init() {
        if (!Files.exists(SOURCE_DIR)) {
            try {
                Files.createDirectories(SOURCE_DIR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateFilesOnStartup() {
        System.out.println("[ZXC] Generating sound pack on startup...");
        performGeneration();
    }

    public static void reloadSounds(MinecraftClient client) {
        ZXCUtils.send("Генерация звуков и перезагрузка...", Formatting.YELLOW);

        CompletableFuture.runAsync(() -> {
            performGeneration();

            client.execute(() -> {
                try {
                    enablePackAndReload(client);
                    ZXCUtils.send("Звуки готовы! Загружено: " + loadedSoundNames.size(), Formatting.GREEN);
                } catch (Exception e) {
                    ZXCUtils.send("Ошибка активации: " + e.getMessage(), Formatting.RED);
                }
            });
        });
    }

    private static void performGeneration() {
        try {
            deleteDirectory(PACK_DIR);

            Path soundsDir = PACK_DIR.resolve("assets/zxchelper/sounds");
            Files.createDirectories(soundsDir);
            createMcMeta();

            loadedSoundNames.clear();
            JsonObject soundsJson = new JsonObject();

            if (Files.exists(SOURCE_DIR)) {
                try (Stream<Path> paths = Files.walk(SOURCE_DIR)) {
                    paths.filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".ogg"))
                            .forEach(sourcePath -> {
                                try {
                                    String fileName = sourcePath.getFileName().toString();
                                    String soundId = fileName.replace(".ogg", "").toLowerCase().replaceAll("[^a-z0-9_]", "_");

                                    Files.copy(sourcePath, soundsDir.resolve(soundId + ".ogg"), StandardCopyOption.REPLACE_EXISTING);

                                    JsonObject soundEntry = new JsonObject();
                                    JsonArray soundsList = new JsonArray();
                                    soundsList.add("zxchelper:" + soundId);
                                    soundEntry.add("sounds", soundsList);

                                    soundsJson.add(soundId, soundEntry);
                                    loadedSoundNames.add(soundId);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }

            try (FileWriter writer = new FileWriter(PACK_DIR.resolve("assets/zxchelper/sounds.json").toFile())) {
                new GsonBuilder().setPrettyPrinting().create().toJson(soundsJson, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enablePackAndReload(MinecraftClient client) {
        ResourcePackManager manager = client.getResourcePackManager();
        manager.scanPacks();
        String packId = "file/" + PACK_NAME;

        if (!manager.getEnabledNames().contains(packId)) {
            List<String> enabledPacks = new ArrayList<>(manager.getEnabledNames());
            enabledPacks.add(packId);
            manager.setEnabledProfiles(enabledPacks);
        }
        client.reloadResources();
    }

    public static void checkAutoEnable(MinecraftClient client) {
        String packId = "file/" + PACK_NAME;
        if (!client.getResourcePackManager().getEnabledNames().contains(packId)) {
            System.out.println("[ZXC] First launch detected, enabling resource pack...");
            enablePackAndReload(client);
        }
    }

    private static void createMcMeta() throws IOException {
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 15);
        pack.addProperty("description", "ZXC Custom Sounds");
        JsonObject root = new JsonObject();
        root.add("pack", pack);
        try (FileWriter writer = new FileWriter(PACK_DIR.resolve("pack.mcmeta").toFile())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted((a, b) -> -a.compareTo(b))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                            }
                        });
            }
        }
    }
}