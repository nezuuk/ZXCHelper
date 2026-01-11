package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.util.ZXCUtils;

import java.util.List;

public class ZVersionCommand extends BaseClientCommand {
    public ZVersionCommand() {
        super("zversion", "узнать версию мода");
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        ZXCUtils.send("версия мода: %s".formatted(ConfigManager.getConfig().getLastInstalledVersion()));

        return 0;
    }
}
