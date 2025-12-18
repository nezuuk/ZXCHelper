package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZUpdateCommand extends BaseClientCommand {

    private static final String UPDATE_URL =
            "https://github.com/nezuuk/ZXCHelper/releases/latest/download/ZXCHelper.jar";

    private static final AtomicBoolean UPDATING = new AtomicBoolean(false);

    public ZUpdateCommand() {
        super("zupdate", "Скачать обновление ZXCHelper", 0, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 0;

        if (!UPDATING.compareAndSet(false, true)) {
            sendChat("Обновление уже выполняется.");
            return 1;
        }

        sendChat("Проверяю обновление и начинаю загрузку...");

        new Thread(() -> {
            try {
                downloadUpdate();
            } finally {
                UPDATING.set(false);
            }
        }, "ZXCHelper-UpdateThread").start();

        return 1;
    }

    private void downloadUpdate() {

        try {
            URL url = new URL(UPDATE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(60_000);
            conn.setRequestProperty("User-Agent", "ZXCHelper-Updater");

            int code = conn.getResponseCode();
            if (code != 200) {
                sendChat("Ошибка загрузки: HTTP " + code);
                return;
            }

            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path modsDir = gameDir.resolve("mods");
            Files.createDirectories(modsDir);

            Path target = modsDir.resolve("zxchelper-update.jar");

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            sendChat("Обновление скачано: " + target.getFileName());
            sendChat("Перезапусти игру и замени старый jar этим файлом.");

        } catch (IOException e) {
            e.printStackTrace();
            sendChat("Ошибка при загрузке обновления: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
        }
    }

    private void sendChat(String msg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player != null) {
                Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
                Text lastmsg = Text.literal(msg).formatted(Formatting.GRAY);
                mc.player.sendMessage(prefix.copy().append(lastmsg.copy()));
            }
        });
    }
}