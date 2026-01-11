package ru.emrass.zxchelper.net.manager.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting; // Импорт для цветов
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.SoundPackGenerator;
import ru.emrass.zxchelper.utils.ZXCUtils; // Импорт твоей утилиты

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;

public class SoundSyncManager extends BaseWsHandler {

    private static final Path SOUNDS_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_sounds");

    public SoundSyncManager() {
        super(WsMessageType.SYNC_DATA);
    }

    public static void uploadSound(String fileName) {
        try {
            if (!fileName.endsWith(".ogg")) fileName += ".ogg";
            Path filePath = SOUNDS_DIR.resolve(fileName);

            if (!Files.exists(filePath)) {
                ZXCUtils.send("Файл не найден: " + fileName, Formatting.RED);
                return;
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String base64 = Base64.getEncoder().encodeToString(fileBytes);

            JsonObject raw = new JsonObject();
            raw.addProperty("filename", fileName);
            raw.addProperty("data", base64);
            ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.UPLOAD_SOUND, raw);

            ZXCUtils.send("Звук " + fileName + " отправлен на сервер!", Formatting.GREEN);

        } catch (IOException e) {
            ZXCUtils.send("Ошибка чтения файла: " + e.getMessage(), Formatting.RED);
            e.printStackTrace();
        }
    }

    public static void requestSync() {
        JsonObject json = new JsonObject();
        ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.REQUEST_SYNC, json);

        ZXCUtils.send("Запрос синхронизации отправлен...", Formatting.YELLOW);
    }

    @Override
    public void handle(JsonObject json) {
        if (!json.has("sounds")) return;

        JsonObject soundsObj = json.getAsJsonObject("sounds");
        int count = 0;

        if (!Files.exists(SOUNDS_DIR)) {
            try { Files.createDirectories(SOUNDS_DIR); } catch (IOException e) {}
        }

        for (Map.Entry<String, JsonElement> entry : soundsObj.entrySet()) {
            String fileName = entry.getKey();
            String base64 = entry.getValue().getAsString();

            try {
                byte[] data = Base64.getDecoder().decode(base64);
                Path filePath = SOUNDS_DIR.resolve(fileName);

                Files.write(filePath, data);
                count++;
                System.out.println("Скачан звук: " + fileName);

            } catch (IOException e) {
                System.err.println("Ошибка сохранения " + fileName);
            }
        }

        ZXCUtils.send("Синхронизация завершена! Скачано файлов: " + count, Formatting.GREEN);

        if (count > 0) {
            MinecraftClient.getInstance().execute(() ->
                    SoundPackGenerator.reloadSounds(MinecraftClient.getInstance())
            );
        }
    }

}