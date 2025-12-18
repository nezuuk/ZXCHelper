package ru.emrass.zxchelper.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class BaseClientCommand {

    private final String name;
    private final String description;
    private final int argCount;
    private final boolean greedyLast;

    protected BaseClientCommand(String name) {
        this.name = name;
        this.description = "testCommand";
        this.argCount = 0;
        this.greedyLast = false;
    }

    protected BaseClientCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.argCount = 0;
        this.greedyLast = false;
    }

    protected BaseClientCommand(String name, String description, int argCount, boolean greedyLast) {
        this.name = name;
        this.description = description;
        this.argCount = argCount;
        this.greedyLast = greedyLast;
    }

    public void registerBrigadier(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root =
                ClientCommandManager.literal(name);

        if (argCount == 0) {
            root.executes(ctx -> execute(ctx.getSource(), Collections.emptyList()));
        } else {
            ArgumentBuilder<FabricClientCommandSource, ?> current = root;

            for (int i = 0; i < argCount; i++) {
                final String argName = "arg" + (i + 1);
                boolean isLast = (i == argCount - 1);

                var argBuilder = ClientCommandManager.argument(
                        argName,
                        (greedyLast && isLast)
                                ? StringArgumentType.greedyString()
                                : StringArgumentType.word()
                );

                if (isLast) {
                    argBuilder
                            .suggests((ctx, builder) -> {
                                List<String> argsSoFar = collectArgsSoFar(builder.getInput());
                                if (!argsSoFar.isEmpty()) {
                                    argsSoFar.set(argsSoFar.size() - 1, builder.getRemaining());
                                } else {
                                    argsSoFar.add(builder.getRemaining());
                                }
                                List<String> suggestions = complete(ctx.getSource(), argsSoFar);
                                return CommandSource.suggestMatching(suggestions, builder);
                            })
                            .executes(ctx -> {
                                List<String> args = collectArgs(ctx.getInput());
                                return execute(ctx.getSource(), args);
                            });
                }

                current.then(argBuilder);
                current = argBuilder;
            }
        }

        dispatcher.register(root);
    }

    protected abstract int execute(FabricClientCommandSource src, List<String> args);

    protected List<String> complete(FabricClientCommandSource src, List<String> argsSoFar) {
        return Collections.emptyList();
    }


    private List<String> collectArgs(String input) {
        String noSlash = input.startsWith("/") ? input.substring(1) : input;
        String[] parts = noSlash.split("\\s+");
        List<String> result = new ArrayList<>();
        if (parts.length <= 1) return result;

        if (argCount == 1 && greedyLast) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) sb.append(' ');
                sb.append(parts[i]);
            }
            result.add(sb.toString());
            return result;
        }

        for (int i = 1; i < parts.length && result.size() < argCount; i++) {
            result.add(parts[i]);
        }
        return result;
    }

    private List<String> collectArgsSoFar(String input) {
        String noSlash = input.startsWith("/") ? input.substring(1) : input;
        String[] parts = noSlash.split("\\s+");
        List<String> result = new ArrayList<>();
        if (parts.length <= 1) return result;

        if (argCount == 1 && greedyLast) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) sb.append(' ');
                sb.append(parts[i]);
            }
            result.add(sb.toString());
            return result;
        }

        for (int i = 1; i < parts.length && result.size() < argCount; i++) {
            result.add(parts[i]);
        }
        return result;
    }
}