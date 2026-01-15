package ru.emrass.zxchelper.pings.skins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.ZXCPaths;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SkinHandler extends BaseWsHandler {
    private static boolean isUploadingAll = false;

    public SkinHandler() {
        super(WsMessageType.SYNC_SKIN);
    }

    public static void uploadAll() {
        ZXCUtils.send("Начинаю проверку и загрузку всех скинов...", Formatting.YELLOW);


        isUploadingAll = true;
        requestSync();
    }

    public static void requestSync() {
        ZXCHelper.getInstance().getWebService().send(WsMessageType.REQUEST_SKINS, new JsonObject());
        ZXCUtils.send("Синхронизация скинов...", Formatting.YELLOW);
    }

    @Override
    public void handle(JsonObject json) {
        if (!json.has("skins")) return;

        Map<String, String> serverSkins = new HashMap<>();
        JsonObject skinsObj = json.get("skins").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : skinsObj.entrySet()) {
            serverSkins.put(entry.getKey(), entry.getValue().getAsString());
        }

        if (isUploadingAll) {
            isUploadingAll = false;

            CompletableFuture.runAsync(() -> {
                int uploaded = 0;
                int skipped = 0;
                int errors = 0;

                try {
                    if (!Files.exists(ZXCPaths.SKINS)) {
                        ZXCUtils.send("Папка skins пуста!", Formatting.RED);
                        return;
                    }

                    List<Path> files;
                    try (var stream = Files.walk(ZXCPaths.SKINS)) {
                        files = stream.filter(Files::isRegularFile)
                                .filter(p -> p.toString().endsWith(".png"))
                                .toList();
                    }

                    ZXCUtils.send("Найдено файлов: " + files.size() + ". Проверка...", Formatting.YELLOW);

                    for (Path path : files) {
                        String name = path.getFileName().toString().replace(".png", "");

                        if (serverSkins.containsKey(name)) {
                            skipped++;
                            continue;
                        }

                        if (Files.size(path) > 85 * 1024) {
                            errors++;
                            ZXCUtils.send("Скип: " + name + " (Слишком большой)", Formatting.RED);
                            continue;
                        }
                        ZXCUtils.send("Загрузка: " + name + "...", Formatting.GRAY);

                        byte[] bytes = Files.readAllBytes(path);
                        String base64 = Base64.getEncoder().encodeToString(bytes);

                        JsonObject uploadJson = new JsonObject();

                        uploadJson.addProperty("name", name);
                        uploadJson.addProperty("data", base64);

                        ZXCHelper.getInstance().getWebService().send(WsMessageType.UPLOAD_SKIN, uploadJson);

                        uploaded++;
                        Thread.sleep(500);
                    }

                    ZXCUtils.send("Завершено! Загружено: " + uploaded + ", Пропущено: " + skipped + ", Ошибок: " + errors, Formatting.GREEN);

                } catch (Exception e) {
                    ZXCUtils.send("Критическая ошибка: " + e.getMessage(), Formatting.RED);
                }
            });
            return;
        }

        ZXCUtils.send("Список получен (" + serverSkins.size() + " шт). Проверка недостающих...", Formatting.GREEN);
        SkinManager.downloadSkins(serverSkins);
    }
}