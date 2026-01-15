package ru.emrass.zxchelper.pings;

import lombok.Data;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

@Data
@Getter
public class Ping {

    private Vec3d pos;
    private String id;
    private final long startTime;
    private final int color;
    private final String skin;
    public final int entityId;

    public Ping(Vec3d pos, int color, String skin, int entityId) {
        this.pos = pos;
        this.id = UUID.randomUUID().toString();
        this.color = color;
        this.skin = skin;
        this.entityId = entityId;
        this.startTime = System.currentTimeMillis();
    }
}
