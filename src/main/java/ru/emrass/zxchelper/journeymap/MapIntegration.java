package ru.emrass.zxchelper.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Displayable;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapImage;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.emrass.zxchelper.net.manager.pings.Ping;

import java.util.HashMap;
import java.util.Map;

@journeymap.client.api.ClientPlugin
public class MapIntegration implements IClientPlugin {

    private static IClientAPI jmAPI;

    private static final Map<String, Displayable> activeWaypoints = new HashMap<>();

    @Override
    public void initialize(IClientAPI api) {
        jmAPI = api;
        System.out.println("[ZXC Helper] Connected to JourneyMap!");
    }

    @Override
    public String getModId() {
        return "zxchelper";
    }

    @Override
    public void onEvent(ClientEvent event) {
    }

    @SneakyThrows
    public static void createWaypoint(Ping ping) {
        if (jmAPI == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        RegistryKey<World> dimension = mc.world.getRegistryKey();

        BlockPos pos = BlockPos.ofFloored(ping.getPos().x, ping.getPos().y, ping.getPos().z);
        int color = ping.getColor();
        MapImage icon = new MapImage(new Identifier("journeymap", "ui/img/waypoint-icon.png"), 32, 32);
        icon.setColor(color);
        icon.setAnchorX(16);
        icon.setAnchorY(16);
        MarkerOverlay marker = new MarkerOverlay(
                "zxchelper",
                ping.getId(),
                pos,
                icon
        );
        marker.setDimension(dimension);
        marker.setTitle("");
        marker.setLabel("");


        jmAPI.show(marker);

        activeWaypoints.put(ping.getId(), marker);

    }

    public static void removeWaypoint(Ping ping) {
        if (jmAPI == null) return;

        Displayable wp = activeWaypoints.remove(ping.getId());
        if (wp != null) {
            jmAPI.remove(wp);
        }
    }

    public static void updatePos(Ping ping) {
        removeWaypoint(ping);
        createWaypoint(ping);
    }
}