package ru.emrass.zxchelper.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.emrass.zxchelper.net.manager.pings.Ping;
import ru.emrass.zxchelper.net.manager.pings.PingManager;


public class PingRenderer {

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(PingRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        if (PingManager.activePings.isEmpty()) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        MinecraftClient client = MinecraftClient.getInstance();

        matrices.push();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (Ping ping : PingManager.activePings) {
            double targetX = ping.getPos().x;
            double targetY = ping.getPos().y;
            double targetZ = ping.getPos().z;

            if (ping.getEntityId() != -1 && client.world != null) {
                Entity targetEntity = client.world.getEntityById(ping.getEntityId());
                if (targetEntity != null && targetEntity.isAlive()) {
                    targetX = targetEntity.getX();
                    targetY = targetEntity.getY() + targetEntity.getHeight() + 0.5;
                    targetZ = targetEntity.getZ();
                    ping.setPos(new Vec3d(targetX, targetY, targetZ));
                }
            }

            matrices.push();
            matrices.translate(targetX - cameraPos.x, targetY - cameraPos.y, targetZ - cameraPos.z);

            int c = ping.getColor();
            float r = ((c >> 16) & 0xFF) / 255f;
            float g = ((c >> 8) & 0xFF) / 255f;
            float b = ((c) & 0xFF) / 255f;

            if (ping.getEntityId() == -1) {
                Matrix4f matrix = matrices.peek().getPositionMatrix();
                buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                drawGlowingRing(buffer, matrix, 0.6f, r, g, b);
                tessellator.draw();
            }

            long time = System.currentTimeMillis();
            float bobbing = (float) Math.sin(time / 200.0) * 0.12f + 0.2f;

            matrices.push();
            matrices.translate(0, bobbing, 0);


            float stemWidth = 0.15f;
            float stemTop = 1.2f;

            matrices.push();
            float arrowRotation = (time % 5000) / 5000.0f * 360.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arrowRotation));

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

            drawArrowShape(buffer, matrix, r, g, b, 0.9f, stemWidth, stemTop);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            matrix = matrices.peek().getPositionMatrix();
            drawArrowShape(buffer, matrix, r, g, b, 0.9f, stemWidth, stemTop);

            tessellator.draw();
            matrices.pop();


            float beamStartY = stemTop;
            float beamHeight = 300.0f;
            float beamRadius = stemWidth;

            matrix = matrices.peek().getPositionMatrix();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            drawCylindricalBeam(buffer, matrix, beamRadius, beamStartY, beamHeight, r, g, b, 0.5f);

            tessellator.draw();

            matrices.pop();
            matrices.pop();
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static void drawCylindricalBeam(BufferBuilder buffer, Matrix4f matrix, float radius, float yStart, float height, float r, float g, float b, float alpha) {
        int segments = 16;
        float yEnd = yStart + height;

        for (int i = 0; i < segments; i++) {
            double angle1 = Math.toRadians((360.0 / segments) * i);
            double angle2 = Math.toRadians((360.0 / segments) * (i + 1));

            float x1 = (float) Math.cos(angle1) * radius;
            float z1 = (float) Math.sin(angle1) * radius;
            float x2 = (float) Math.cos(angle2) * radius;
            float z2 = (float) Math.sin(angle2) * radius;

            buffer.vertex(matrix, x1, yStart, z1).color(r, g, b, alpha).next();
            buffer.vertex(matrix, x2, yStart, z2).color(r, g, b, alpha).next();

            buffer.vertex(matrix, x2, yEnd, z2).color(r, g, b, 0.0f).next();
            buffer.vertex(matrix, x1, yEnd, z1).color(r, g, b, 0.0f).next();
        }
    }

    private static void drawArrowShape(BufferBuilder buffer, Matrix4f matrix, float r, float g, float b, float a, float stemWidth, float stemTop) {
        float headBase = 0.5f;
        float headWidth = 0.45f;
        float tipY = 0.0f;

        vertex(buffer, matrix, -stemWidth, headBase, r, g, b, a);
        vertex(buffer, matrix, stemWidth, headBase, r, g, b, a);
        vertex(buffer, matrix, -stemWidth, stemTop, r, g, b, a);

        vertex(buffer, matrix, stemWidth, headBase, r, g, b, a);
        vertex(buffer, matrix, stemWidth, stemTop, r, g, b, a);
        vertex(buffer, matrix, -stemWidth, stemTop, r, g, b, a);

        vertex(buffer, matrix, -headWidth, headBase, r, g, b, a);
        vertex(buffer, matrix, headWidth, headBase, r, g, b, a);
        vertex(buffer, matrix, 0, tipY, r, g, b, a);
    }

    private static void vertex(BufferBuilder buffer, Matrix4f matrix, float x, float y, float r, float g, float b, float a) {
        buffer.vertex(matrix, x, y, 0).color(r, g, b, a).next();
    }

    private static void drawGlowingRing(BufferBuilder buffer, Matrix4f matrix, float radius, float r, float g, float b) {
        int segments = 32;
        for (int i = 0; i <= segments; i++) {
            double angle = Math.toRadians((360.0 / segments) * i);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            buffer.vertex(matrix, cos * 0.1f, 0.0f, sin * 0.1f).color(r, g, b, 0.1f).next();
            buffer.vertex(matrix, cos * radius, 0.0f, sin * radius).color(r, g, b, 0.8f).next();
        }
    }
}