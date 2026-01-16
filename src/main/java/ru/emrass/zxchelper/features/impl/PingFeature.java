package ru.emrass.zxchelper.features.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.features.BaseFeature;
import ru.emrass.zxchelper.pings.manager.PingManager;


public class PingFeature extends BaseFeature {
    private final PingManager pingManager = ZXCHelper.getInstance().getPingManager();
    private final double MAX_DIST = 32.0;

    public PingFeature() {
        super("ping", "Пинги", "Поставить метку на расстоянии", GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    public void onKeyPressed(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        TargetInfo target = traceForPing(client, MAX_DIST);

        if (target == null) return;

        pingManager.addPing(target.pos.x, target.pos.y, target.pos.z, target.isEnemy,target.entityId);

        client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 1.0f, 1.5f);


    }

    private record TargetInfo(Vec3d pos, boolean isEnemy, int entityId) {}

    private TargetInfo traceForPing(MinecraftClient client, double dist) {
        Entity camera = client.getCameraEntity();
        if (camera == null) return null;

        Vec3d start = camera.getCameraPosVec(1.0F);
        Vec3d direction = camera.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(dist));

        HitResult blockHit = client.world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                camera
        ));

        double blockDist = dist;
        if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            blockDist = blockHit.getPos().distanceTo(start);
            end = blockHit.getPos();
        }

        Box box = camera.getBoundingBox().stretch(direction.multiply(blockDist)).expand(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHit = ProjectileUtil.raycast(
                camera,
                start,
                end,
                box,
                (entity) -> !entity.isSpectator() && entity.canHit(),
                blockDist * blockDist
        );


        if (entityHit != null) {
            return new TargetInfo(entityHit.getPos(), true, entityHit.getEntity().getId());
        }

        if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            return new TargetInfo(blockHit.getPos(), false, -1);
        }

        return null;
    }
}