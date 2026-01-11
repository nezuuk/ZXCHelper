package ru.emrass.zxchelper.net.manager.pings;

import lombok.Data;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;
@Data
@Getter
public class Ping {

    private Vec3d pos;
    private final long startTime;
    private final int color;
    public final int entityId;

    public Ping(Vec3d pos, int color, int entityId) {
        this.pos = pos;
        this.color = color;
        this.entityId = entityId;
        this.startTime = System.currentTimeMillis();
    }
}
