package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;

public class ZRemoveFriendCommand extends BaseClientCommand {

    public ZRemoveFriendCommand() {
        super("zremove", "Удалить игрока из списка друзей", 1, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if (src.getPlayer() == null || args.isEmpty()) return 0;

        String nick = args.get(0);
        boolean removed = ConfigManager.removeFriend(nick);

        Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
        Text name   = Text.literal(nick).formatted(Formatting.GRAY);
        Text status = Text.literal(removed ? " удалён из друзей" : " не найден в друзьях")
                .formatted(removed ? Formatting.RED : Formatting.YELLOW);

        src.getPlayer().sendMessage(prefix.copy().append(name).append(status));
        return 1;
    }
    @Override
    protected List<String> complete(FabricClientCommandSource src, List<String> argsSoFar) {
        // подсказки по тексту по желанию
        return ConfigManager.getFriends().stream().toList();
    }
}