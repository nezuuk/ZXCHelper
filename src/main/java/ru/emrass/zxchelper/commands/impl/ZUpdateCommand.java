package ru.emrass.zxchelper.commands.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.config.ConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZUpdateCommand extends BaseClientCommand {

    private static final String GITHUB_API_LATEST =
            "https://api.github.com/repos/nezuuk/ZXCHelper/releases/latest";

    private static final String UPDATE_URL =
            "https://github.com/nezuuk/ZXCHelper/releases/latest/download/ZXCHelper.jar";

    private static final AtomicBoolean UPDATING = new AtomicBoolean(false);

    public ZUpdateCommand() {
        super("zupdate", "Скачать обновление ZXCHelper", 0, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if (!UPDATING.compareAndSet(false, true)) {
            sendChat("Обновление уже выполняется.");
            return 1;
        }

        sendChat("Проверяю наличие обновления...");

        new Thread(() -> {
            try {
                doUpdateFlow();
            } finally {
                UPDATING.set(false);
            }
        }, "ZXCHelper-UpdateThread").start();

        return 1;
    }

    private void doUpdateFlow() {
        String currentInstalled = ConfigManager.getInstalledVersion();
        String latestTag = fetchLatestReleaseTag();

        if (latestTag != null) {
            if (latestTag.equals(currentInstalled)) {
                sendChat("У тебя уже последняя версия ZXCHelper (" + latestTag + ").");
                return;
            } else {
                if (currentInstalled != null) {
                    sendChat("Найдена новая версия: " + latestTag + " (установлена " + currentInstalled + ").");
                } else {
                    sendChat("Найдена версия: " + latestTag + ".");
                }
                sendChat("Начинаю загрузку обновления...");
            }
        } else {
            sendChat("Не удалось проверить последнюю версию, пробую всё равно скачать обновление.");
        }

        boolean ok = downloadUpdate();
        if (ok && latestTag != null) {
            ConfigManager.setInstalledVersion(latestTag);
            sendChat("Версия " + latestTag + " отмечена как установленная.");
        }
    }

    private String fetchLatestReleaseTag() {
        try {
            URL url = new URL(GITHUB_API_LATEST);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            conn.setRequestProperty("User-Agent", "ZXCHelper-Updater");
            conn.setRequestProperty("Accept", "application/vnd.github+json");

            int code = conn.getResponseCode();
            if (code != 200) {
                sendChat("GitHub API вернул HTTP " + code + " при проверке версии.");
                return null;
            }

            try (InputStream in = conn.getInputStream();
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {

                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("tag_name")) return null;

                return root.get("tag_name").getAsString();
            }
        } catch (Exception e) {
            sendChat("Ошибка при проверке версии: " + e.getClass().getSimpleName()
                    + " - " + safeMsg(e.getMessage()));
            return null;
        }
    }

    private boolean downloadUpdate() {
        try {
            URL url = new URL(UPDATE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(60_000);
            conn.setRequestProperty("User-Agent", "ZXCHelper-Updater");

            int code = conn.getResponseCode();
            if (code != 200) {
                sendChat("Ошибка загрузки: HTTP " + code);
                return false;
            }

            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path modsDir = gameDir.resolve("mods");
            Files.createDirectories(modsDir);

            Path target = modsDir.resolve("zxchelper-update.jar");

            Path currentJar = getCurrentJarPath();
            if (currentJar != null) {
                try {
                    if (Files.isSameFile(currentJar, target)) {
                        target = modsDir.resolve("zxchelper-update-new.jar");
                    }
                } catch (IOException ignored) {
                }
            }

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            sendChat("Обновление скачано в: " + target.getFileName());
            sendChat("Перед следующим запуском оставь в папке mods только новую версию ZXCHelper.");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            sendChat("Ошибка при загрузке обновления: " + e.getClass().getSimpleName()
                    + " - " + safeMsg(e.getMessage()));
            return false;
        }
    }

    private Path getCurrentJarPath() {
        try {
            URI uri = ZXCHelper.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();

            if (!"file".equalsIgnoreCase(uri.getScheme())) {
                return null;
            }
            Path path = Paths.get(uri);
            if (path.toString().endsWith(".jar")) {
                return path;
            }
        } catch (Exception ignored) {
        }
        return null;
    }


    private void sendChat(String msg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player != null) {
                Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
                mc.player.sendMessage(prefix.copy().append(Text.literal(msg)));
            }
        });
    }

    private String safeMsg(String m) {
        return m == null ? "" : m;
    }
}