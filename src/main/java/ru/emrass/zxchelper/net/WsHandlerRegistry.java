package ru.emrass.zxchelper.net;

import lombok.experimental.UtilityClass;
import ru.emrass.zxchelper.ZXCHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@UtilityClass
public final class WsHandlerRegistry {

    public static final List<BaseWsHandler> HANDLERS = new ArrayList<>();
    private static boolean initialized = false;

    public static void registerHandlers(BaseWsHandler... handlers) {
        HANDLERS.addAll(Arrays.asList(handlers));
        init(ZXCHelper.getInstance().getWebService());
    }

    public static void init(WsService wsService) {
        if (initialized) {
            return;
        }
        initialized = true;

        for (BaseWsHandler handler : HANDLERS) {
            wsService.registerHandler(handler.getType(), handler::handle);
        }
    }


}