package ru.emrass.zxchelper.features.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.features.ToggleFeature;

/**
 * GLOW подсветка игроков:
 *  - друзья из конфига — зелёный контур;
 *  - остальные игроки — красный контур.
 *
 * Реализовано через:
 *  - client-side scoreboard команды (цвет контура);
 *  - миксин в Entity.isGlowing() (сам факт свечения).
 *
 * МОЖЕТ конфликтовать с серверным scoreboard (цвет ников/команды).
 */
public class GlowHighlightFeature extends ToggleFeature {

    public static final String TEAM_FRIENDS_ID = "ZXCHelper_FRIENDS";
    public static final String TEAM_ENEMIES_ID = "ZXCHelper_ENEMIES";

    public GlowHighlightFeature() {
        super("glow", "GLOW подсветка",
                "Друзья зелёным, остальные красным", GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    protected void onEnabledTick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        Scoreboard scoreboard = client.world.getScoreboard();

        Team friendsTeam = getOrCreateTeam(scoreboard, TEAM_FRIENDS_ID, Formatting.GREEN);
        Team enemiesTeam = getOrCreateTeam(scoreboard, TEAM_ENEMIES_ID, Formatting.RED);

        client.world.getPlayers().forEach(player -> {
            if (player == client.player) return;
            if (!isRealPlayer(player)) return;
            String name = player.getGameProfile().getName();
            boolean isFriend = ConfigManager.isFriend(name);

            Team targetTeam = isFriend ? friendsTeam : enemiesTeam;

            // addPlayerToTeam сам перенесёт игрока из любой старой команды
            scoreboard.addPlayerToTeam(name, targetTeam);
        });
    }

    /** Создаёт/находит команду и задаёт цвет (для ника и контура). */
    private Team getOrCreateTeam(Scoreboard scoreboard, String id, Formatting color) {
        Team team = scoreboard.getTeam(id);
        if (team == null) {
            team = scoreboard.addTeam(id);
        }
        team.setColor(color);
        team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER);
        return team;
    }

    public boolean isRealPlayer(PlayerEntity player) {
        return !player.getName().getString().contains("§");
//        return !player.getName().getString().replaceAll("§.", "").startsWith("NPC");
    }
}