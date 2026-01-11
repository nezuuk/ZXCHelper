package ru.emrass.zxchelper.net.manager.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.AudioConverter;
import ru.emrass.zxchelper.utils.SoundUtils;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SoundSyncManager extends BaseWsHandler {

    private static final Path SOUNDS_DIR = FabricLoader.getInstance().getGameDir().resolve("zxc_sounds");

    // Список поддерживаемых расширений для загрузки
    private static final String[] EXTENSIONS = {".ogg", ".mp3", ".mp4", ".wav", ".m4a"};

    public SoundSyncManager() {
        super(WsMessageType.SYNC_DATA);
    }

    public static void uploadSound(String inputName) {
        try {
            Path finalPath = null;
            String finalName = null;

            Path directPath = SOUNDS_DIR.resolve(inputName);
            if (Files.exists(directPath)) {
                finalPath = directPath;
                finalName = inputName;
            } else {
                for (String ext : EXTENSIONS) {
                    Path tryPath = SOUNDS_DIR.resolve(inputName + ext);
                    if (Files.exists(tryPath)) {
                        finalPath = tryPath;
                        finalName = inputName + ext;
                        break;
                    }
                }
            }

            // Если так и не нашли
            if (finalPath == null) {
                ZXCUtils.send("Файл не найден! (Искал: " + inputName + " с .ogg/.mp3/.mp4...)", Formatting.RED);
                return;
            }

            byte[] fileBytes = Files.readAllBytes(finalPath);
            String base64 = Base64.getEncoder().encodeToString(fileBytes);

            JsonObject raw = new JsonObject();
            raw.addProperty("filename", finalName);
            raw.addProperty("data", base64);
            ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.UPLOAD_SOUND, raw);

            ZXCUtils.send("Файл " + finalName + " отправлен на сервер!", Formatting.GREEN);

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
                System.out.println("Скачан файл: " + fileName);

            } catch (IOException e) {
                System.err.println("Ошибка сохранения " + fileName);
            }
        }

        ZXCUtils.send("Скачано файлов: " + count + ". Начинаю обработку...", Formatting.GREEN);

        if (count > 0) {
            MinecraftClient.getInstance().execute(() -> {
                CompletableFuture.runAsync(() -> {
                    AudioConverter.process();

                    SoundUtils.refreshSoundList();

                    ZXCUtils.send("Звуки готовы к использованию!", Formatting.GREEN);
                });
            });
        }
    }
}