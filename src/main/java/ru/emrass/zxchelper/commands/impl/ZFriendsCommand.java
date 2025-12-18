package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;
import java.util.Set;

public class ZFriendsCommand extends BaseClientCommand {
    public ZFriendsCommand() {
        super("zfriends", "Показать список друзей", 0, false);
    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if (src.getPlayer() == null) return 0;

        Set<String> friends = ConfigManager.getFriends();

        Text header = Text.literal("✌ Друзья (" + friends.size() + "):")
                .formatted(Formatting.GOLD);
        src.getPlayer().sendMessage(header);

        if (friends.isEmpty()) {
            src.getPlayer().sendMessage(
                    Text.literal("  (пусто)").formatted(Formatting.GRAY)
            );
            return 1;
        }

        friends.stream().sorted().forEach(nick ->
                src.getPlayer().sendMessage(
                        Text.literal("  " + nick).formatted(Formatting.GRAY)
                )
        );

        return 1;
    }
}
