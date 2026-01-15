package ru.emrass.zxchelper.pings.skins;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import ru.emrass.zxchelper.pings.skins.utils.SkinModelCache;
import ru.emrass.zxchelper.utils.ZXCPaths;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinManager {

    public static final List<String> availableSkins = new ArrayList<>();
    private static final Map<String, Identifier> textureIds = new HashMap<>();
    private static final Map<String, NativeImage> pixelData = new HashMap<>();

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("[ZXC] Игра запустилась, загружаю скины...");
            SkinManager.loadLocalSkins();
            SkinHandler.requestSync();
        });
    }

    public static void loadLocalSkins() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.getTextureManager() == null) return;
        SkinModelCache.clear();
        pixelData.values().forEach(NativeImage::close);
        pixelData.clear();
        textureIds.clear();

        availableSkins.clear();

        try {
            Files.walk(ZXCPaths.SKINS)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(path -> {
                        String name = path.getFileName().toString().replace(".png", "");
                        registerTexture(name, path);
                        ZXCUtils.send("Скин: %s добавлен!".formatted(name));
                        availableSkins.add(name);
                    });

            System.out.println("[ZXC SKINS] Loaded " + availableSkins.size() + " skins.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerTexture(String name, Path path) {
        try {
            FileInputStream input = new FileInputStream(path.toFile());
            NativeImage image = NativeImage.read(input);
            pixelData.put(name, image);

            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            Identifier id = new Identifier("zxchelper", "custom_skins/" + name);

            MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);

            textureIds.put(name, id);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Identifier getTexture(String name) {
        if (name == null || name.equals("default") || !textureIds.containsKey(name)) {
            return null;
        }
        return textureIds.get(name);
    }

    public static NativeImage getImageData(String name) {
        if (name == null || name.equals("default")) {
            return null;
        }
        return pixelData.get(name);
    }

    public static boolean deleteSkin(String name) {

        Path path = ZXCPaths.SKINS.resolve(name + ".png");
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (!deleted) return false;

            System.out.println("[ZXC SKINS] Удален файл: " + name);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (pixelData.containsKey(name)) {
            NativeImage img = pixelData.remove(name);
            img.close();
        }

        textureIds.remove(name);

        availableSkins.remove(name);

        return true;
    }
    public static void downloadSkins(Map<String, String> skinsMap) {
        new Thread(() -> {
            int count = 0;
            int errors = 0;


            for (Map.Entry<String, String> entry : skinsMap.entrySet()) {
                String name = entry.getKey();
                String base64Data = entry.getValue();

                Path filePath = ZXCPaths.SKINS.resolve(name + ".png");

                if (!Files.exists(filePath)) {
                    try {
                        byte[] data = java.util.Base64.getDecoder().decode(base64Data);
                        Files.write(filePath, data);

                        count++;
                        System.out.println("[ZXC] Saved skin: " + name);
                    } catch (Exception e) {
                        errors++;
                        System.err.println("[ZXC] Failed to save " + name + ": " + e.getMessage());
                    }
                }
            }

            if (count > 0) {
                System.out.println("[ZXC] Sync finished. New files: " + count + ". Reloading textures...");
                MinecraftClient.getInstance().execute(SkinManager::loadLocalSkins);
            } else if (errors > 0) {
                System.out.println("[ZXC] Sync finished with " + errors + " errors.");
            }
        }).start();
    }
}