package ru.emrass.zxchelper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import ru.emrass.zxchelper.ZXCHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

@Slf4j
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("zxc.json");

    @Getter
    private static ZXCHelperConfig config = new ZXCHelperConfig();

    static {
        load();
    }

    public static void load() {
        if (!Files.exists(FILE)) {
            log.info("[{}] Config file not found, using defaults", ZXCHelper.MOD_NAME);
            return;
        }
        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            ZXCHelperConfig loaded = GSON.fromJson(reader, ZXCHelperConfig.class);
            if (loaded != null) {
                config = loaded;
            }
            log.info("[{}] Config loaded from {}", ZXCHelper.MOD_NAME, FILE);
        } catch (IOException e) {
            log.error("[{}] Failed to load config {}", ZXCHelper.MOD_NAME, FILE, e);
        }
    }

    public static void save() {
        try {
            if (!Files.exists(FILE.getParent())) {
                Files.createDirectories(FILE.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
            log.info("[{}] Config saved to {}", ZXCHelper.MOD_NAME, FILE);
        } catch (IOException e) {
            log.error("[{}] Failed to save config {}", ZXCHelper.MOD_NAME, FILE, e);
        }
    }


    public static boolean isFriend(String name) {
        if (name == null) return false;
        return config.getFriends().contains(name.toLowerCase());
    }

    public static boolean addFriend(String name) {
        if (name == null || name.isBlank()) return false;
        boolean added = config.getFriends().add(name.toLowerCase());
        if (added) save();


        return added;
    }

    public static boolean removeFriend(String name) {
        if (name == null || name.isBlank()) return false;
        boolean removed = config.getFriends().remove(name.toLowerCase());
        if (removed) save();

        return removed;
    }

    public static Set<String> getFriends() {
        return Collections.unmodifiableSet(config.getFriends());
    }
}