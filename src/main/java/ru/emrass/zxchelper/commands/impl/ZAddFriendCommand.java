package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;

public class ZAddFriendCommand extends BaseClientCommand {

    public ZAddFriendCommand() {
        super("zadd", "Добавить игрока в список друзей", 1, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if (src.getPlayer() == null || args.isEmpty()) return 0;

        String nick = args.get(0);
        boolean added = ConfigManager.addFriend(nick);

        Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
        Text name   = Text.literal(nick).formatted(Formatting.GRAY);
        Text status = Text.literal(added ? " добавлен в друзья" : " уже в друзьях")
                .formatted(added ? Formatting.GREEN : Formatting.YELLOW);
        src.getPlayer().sendMessage(prefix.copy().append(name).append(status));
        return 1;
    }
}