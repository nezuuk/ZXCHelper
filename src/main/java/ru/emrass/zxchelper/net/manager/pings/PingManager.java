package ru.emrass.zxchelper.net.manager.pings;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.journeymap.MapIntegration;
import ru.emrass.zxchelper.net.BaseWsHandler;
import ru.emrass.zxchelper.net.WsMessageType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingManager extends BaseWsHandler {
    public static final List<Ping> activePings = new CopyOnWriteArrayList<>();

    public PingManager() {
        super(WsMessageType.PING);
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    @Override
    public void handle(JsonObject json) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        double x = json.has("x") ? json.get("x").getAsDouble() : 0;
        double y = json.has("y") ? json.get("y").getAsDouble() : 0;
        double z = json.has("z") ? json.get("z").getAsDouble() : 0;
        int color = json.has("color") ? json.get("color").getAsInt() : 0;
        int entityID = json.has("entityid") ? json.get("entityid").getAsInt() : -1;

        String action = json.has("action") ? json.get("action").getAsString() : "ADD";

        Vec3d pingPos = new Vec3d(x, y, z);

        if (action.equals("REMOVE")) {
            for (Ping existingPing : activePings) {
                boolean matchEntity = (entityID != -1 && existingPing.getEntityId() == entityID);
                boolean matchPos = (entityID == -1 && existingPing.getPos().squaredDistanceTo(pingPos) < 1.0);

                if (matchEntity || matchPos) {
                    activePings.remove(existingPing);
                    if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                        MapIntegration.removeWaypoint(existingPing);
                    }
                    player.playSound(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.2f);
                    return;
                }
            }

            return;
        }

        if (action.equals("ADD")) {
            for (Ping existingPing : activePings) {
                if (entityID != -1 && existingPing.getEntityId() == entityID) return;
            }
            Ping ping = new Ping(pingPos, color, entityID);

            if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                MapIntegration.createWaypoint(ping);
            }
            if (player.getPos().squaredDistanceTo(pingPos) > 3600) return;
            activePings.add(ping);
            player.playSound(net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 1.0f, 1.5f);
        }
    }

    public void addPing(double x, double y, double z, boolean isEnemy, int entityId) {
        int color = isEnemy ? 0xFFFF0000 : ConfigManager.getConfig().getPingColor();
        Ping ping = new Ping(new Vec3d(x, y, z), color, entityId);
        String action = "ADD";

        if (entityId != -1) {
            for (Ping existing : activePings) {
                if (existing.getEntityId() == entityId) {
                    action = "REMOVE";
                    break;
                }
            }
        }

        sendPing(ping, action);
    }

    private void sendPing(Ping ping, String action) {
        JsonObject json = new JsonObject();
        json.addProperty("x", ping.getPos().getX());
        json.addProperty("y", ping.getPos().getY());
        json.addProperty("z", ping.getPos().getZ());
        json.addProperty("color", ping.getColor());
        json.addProperty("entityid", ping.getEntityId());
        json.addProperty("action", action);

        ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.PING, json);
    }

    private void tick(MinecraftClient client) {
        long now = System.currentTimeMillis();

        activePings.removeIf(ping -> {
            boolean shouldRemove = false;

            if (ping.getEntityId() != -1) {
                if (client.world == null) shouldRemove = true;
                else {
                    Entity entity = client.world.getEntityById(ping.getEntityId());
                    if (entity == null || !entity.isAlive()) shouldRemove = true;

                     if (!shouldRemove && FabricLoader.getInstance().isModLoaded("journeymap")) {
                         MapIntegration.updatePos(ping);
                     }
                }
            } else {
                if ((now - ping.getStartTime()) > 5000) shouldRemove = true;
            }

            if (shouldRemove) {
                if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                    MapIntegration.removeWaypoint(ping);
                }
            }

            return shouldRemove;
        });
    }
}