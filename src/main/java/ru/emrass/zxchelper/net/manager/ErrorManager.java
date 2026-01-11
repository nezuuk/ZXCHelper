package ru.emrass.zxchelper.net.manager;

import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.ZXCUtils;

public class ErrorManager extends BaseWsHandler {

    public ErrorManager() {
        super(WsMessageType.ERROR);
    }

    @Override
    public void handle(JsonObject json) {

        Text prefix = Text.literal("ERROR").formatted(Formatting.RED);

        Text colon = Text.literal(": ");
        Text errorText = Text.literal(json.toString()).formatted(Formatting.GRAY);

        Text full = prefix.copy()
                .append(colon)
                .append(errorText);
        ZXCUtils.send(full);
    }
}
