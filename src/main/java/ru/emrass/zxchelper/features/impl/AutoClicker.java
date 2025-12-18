package ru.emrass.zxchelper.features.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.emrass.zxchelper.events.ChatChannel;
import ru.emrass.zxchelper.events.FabricEvents;
import ru.emrass.zxchelper.features.ToggleFeature;


public class AutoClicker extends ToggleFeature {

    private int tickCounter = 0;
    private final int intervalTicks = 4; // раз в 4 тика

    public AutoClicker() {
        super("autoclicker", "Авто-Кликер",
                "Кликает!", GLFW.GLFW_KEY_UNKNOWN);
        hookBloodCurseDisable();
    }

    @Override
    protected void onEnabledTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null || client.world == null) return;

        tickCounter++;
        if (tickCounter < intervalTicks) {
            return;
        }
        tickCounter = 0;

        HitResult hit = client.crosshairTarget;
        if (hit == null) {
            client.player.swingHand(Hand.MAIN_HAND);
            return;
        }

        switch (hit.getType()) {
            case ENTITY -> {
                EntityHitResult ehr = (EntityHitResult) hit;
                if (ehr.getEntity() != null) {
                    client.interactionManager.attackEntity(client.player, ehr.getEntity());
                    client.player.swingHand(Hand.MAIN_HAND);
                }
            }
            case BLOCK -> {
                BlockHitResult bhr = (BlockHitResult) hit;
                if (!client.world.isAir(bhr.getBlockPos())) {
                    client.interactionManager.attackBlock(bhr.getBlockPos(), bhr.getSide());
                    client.player.swingHand(Hand.MAIN_HAND);
                }
            }
            case MISS -> {
                // нет цели — просто Swing
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private void hookBloodCurseDisable() {
        final String CURSE_TEXT = "На вас наложено проклятие крови, вы получаете урон за каждую нанесенную атаку!";

        FabricEvents.onChatReceived((channel, text) -> {
            if (channel != ChatChannel.CHAT) return;

            String raw = text.getString().toLowerCase();
            if (!raw.contains(CURSE_TEXT)) return;

            MinecraftClient client = MinecraftClient.getInstance();
            setEnabled(false, client, false);

        });
    }
}