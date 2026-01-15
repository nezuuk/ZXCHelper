package ru.emrass.zxchelper.commands.impl;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.commands.BaseClientCommand;
import ru.emrass.zxchelper.commands.CommandRegistry;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;

public class ZHelpCommand extends BaseClientCommand {

    private static final int CMDS_PER_PAGE = 8; // Команд на странице

    public ZHelpCommand() {
        super("zhelp", "Показать все команды ZXCHelper", 1, false);

    }

    @Override
    protected int execute(FabricClientCommandSource src, List<String> args) {
        if (src.getPlayer() == null) return 0;

        List<BaseClientCommand> allCommands = CommandRegistry.getCommands();
        int totalCmds = allCommands.size();
        int totalPages = (int) Math.ceil((double) totalCmds / CMDS_PER_PAGE);

        int page = 1;
        if (!args.isEmpty()) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {}
        }

        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int start = (page - 1) * CMDS_PER_PAGE;
        int end = Math.min(start + CMDS_PER_PAGE, totalCmds);

        Text prefix = Text.literal(ZXCHelper.CHAT_PREFIX).formatted(Formatting.GOLD);
        src.getPlayer().sendMessage(Text.empty());

        Text header = prefix.copy()
                .append(Text.literal(" Команды (Стр. " + page + "/" + totalPages + ")")
                        .formatted(Formatting.YELLOW, Formatting.BOLD));
        src.getPlayer().sendMessage(header);

        for (int i = start; i < end; i++) {
            BaseClientCommand cmd = allCommands.get(i);

            Text cmdText = Text.literal("/" + cmd.getName())
                    .formatted(Formatting.AQUA)
                    .styled(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + cmd.getName()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Нажми, чтобы вставить"))));

            Text line = Text.literal(" » ").formatted(Formatting.DARK_GRAY)
                    .append(cmdText)
                    .append(Text.literal(" - ").formatted(Formatting.GRAY))
                    .append(Text.literal(cmd.getDescription()).formatted(Formatting.WHITE));

            src.getPlayer().sendMessage(line);
        }

        Text nav = Text.literal("\n");

        if (page > 1) {
            int finalPage = page;
            nav.getSiblings().add(
                    Text.literal("[<< Назад] ")
                            .formatted(Formatting.GOLD, Formatting.BOLD)
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zhelp " + (finalPage - 1))))
            );
        } else {
            nav.getSiblings().add(Text.literal("[<< Назад] ").formatted(Formatting.DARK_GRAY));
        }

        nav.getSiblings().add(Text.literal(" --- ").formatted(Formatting.GRAY));

        if (page < totalPages) {
            int finalPage1 = page;
            nav.getSiblings().add(
                    Text.literal(" [Вперед >>]")
                            .formatted(Formatting.GOLD, Formatting.BOLD)
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zhelp " + (finalPage1 + 1))))
            );
        } else {
            nav.getSiblings().add(Text.literal(" [Вперед >>]").formatted(Formatting.DARK_GRAY));
        }

        src.getPlayer().sendMessage(nav);
        return 1;
    }
}