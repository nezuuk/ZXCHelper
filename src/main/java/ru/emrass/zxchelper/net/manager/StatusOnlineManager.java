package ru.emrass.zxchelper.net.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.ZXCUtils;

import java.lang.reflect.Type;
import java.util.List;

public class StatusOnlineManager extends BaseWsHandler {
    private final Type listType = new TypeToken<List<String>>() {
    }.getType();

    public StatusOnlineManager() {
        super(WsMessageType.ONLINE);
    }

    @Override
    public void handle(JsonObject json) {
        JsonArray jsonArray = json.get("users").getAsJsonArray();
        List<String> userList = new Gson().fromJson(jsonArray, listType);

        String usersString = String.join(", ", userList);
        Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);

        Text header = Text.literal("✌ Друзья (" + userList.size() + "):")
                .formatted(Formatting.GOLD);



        Text full = prefix.copy()
                .append(usersString);
        ZXCUtils.send(header);
        ZXCUtils.send(full);

    }
}
