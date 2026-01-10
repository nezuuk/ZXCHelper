package ru.emrass.zxchelper.commands.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.util.ZXCUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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
            ZXCUtils.send("Обновление уже выполняется.", Formatting.GRAY);
            return 1;
        }

        ZXCUtils.send("Проверяю наличие обновления...", Formatting.GRAY);

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
                ZXCUtils.send("У тебя уже последняя версия ZXCHelper (" + latestTag + ").", Formatting.GRAY);
                return;
            } else {
                if (currentInstalled != null) {
                    ZXCUtils.send("Найдена новая версия: " + latestTag + " (установлена " + currentInstalled + ").", Formatting.GRAY);
                } else {
                    ZXCUtils.send("Найдена версия: " + latestTag + ".", Formatting.GRAY);
                }
                ZXCUtils.send("Начинаю загрузку обновления...", Formatting.GRAY);
            }
        } else {
            ZXCUtils.send("Не удалось проверить последнюю версию, пробую скачать обновление.", Formatting.GRAY);
        }

        boolean ok = downloadUpdate();
        if (ok && latestTag != null) {
            ConfigManager.setInstalledVersion(latestTag);
            ZXCUtils.send("Версия " + latestTag + " отмечена как установленная.", Formatting.GREEN);
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
                ZXCUtils.send("GitHub API вернул HTTP " + code + " при проверке версии.", Formatting.RED);
                return null;
            }

            try (InputStream in = conn.getInputStream();
                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {

                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("tag_name")) return null;

                return root.get("tag_name").getAsString();
            }
        } catch (Exception e) {
            ZXCUtils.send("Ошибка при проверке версии: " + e.getClass().getSimpleName()
                    + " - " + safeMsg(e.getMessage()), Formatting.RED);
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
                ZXCUtils.send("Ошибка загрузки: HTTP " + code, Formatting.RED);
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
                        Path alt = modsDir.resolve("zxchelper-update-new.jar");
                        ZXCUtils.send("Игра запущена из zxchelper-update.jar, новая версия будет сохранена в "
                                + alt.getFileName(), Formatting.GRAY);
                        target = alt;
                    }
                } catch (IOException ignored) {
                }
            }

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            ZXCUtils.send("Обновление скачано в: " + target.getFileName(), Formatting.GREEN);
            ZXCUtils.send("Перед следующим запуском оставь в папке mods только новую версию ZXCHelper.", Formatting.GRAY);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            ZXCUtils.send("Ошибка при загрузке обновления: " + e.getClass().getSimpleName()
                    + " - " + safeMsg(e.getMessage()), Formatting.RED);
            return false;
        }
    }

    private Path getCurrentJarPath() {
        try {
            URI uri = ru.emrass.zxchelper.ZXCHelper.class
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

    private String safeMsg(String m) {
        return m == null ? "" : m;
    }
}