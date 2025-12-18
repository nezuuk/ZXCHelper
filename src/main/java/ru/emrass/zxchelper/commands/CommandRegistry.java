package ru.emrass.zxchelper.commands;

import com.mojang.brigadier.CommandDispatcher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.emrass.zxchelper.ZXCHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Простой и надёжный реестр клиентских команд ZXCHelper.
 *
 * В ZXCHelper.onInitializeClient():
 *
 *   CommandRegistry.init();
 *   CommandRegistry.register(new SendCommand());
 *   CommandRegistry.register(new ZHelpCommand());
 *
 * Больше нигде dispatcher не трогаешь.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandRegistry {

    @Getter
    private static final List<BaseClientCommand> commands = new ArrayList<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher,  registryAccess) -> {
                    // сортируем по имени для /zhelp
                    commands.sort(Comparator.comparing(BaseClientCommand::getName));
                    for (BaseClientCommand cmd : commands) {
                        cmd.registerBrigadier(dispatcher);
                        log.info("[{}] Registered /{} - {}", ZXCHelper.MOD_NAME,
                                cmd.getName(), cmd.getDescription());
                    }
                }
        );
    }

    public static void register(BaseClientCommand command) {
        init();
        commands.add(command);
    }

    public static void registerCommands(BaseClientCommand... cmds) {
        Arrays.stream(cmds).forEach(CommandRegistry::register);
    }
}