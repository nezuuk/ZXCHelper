package ru.emrass.zxchelper.pings.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.emrass.zxchelper.pings.Ping;
import ru.emrass.zxchelper.pings.manager.PingManager;
import ru.emrass.zxchelper.pings.skins.SkinManager;
import ru.emrass.zxchelper.pings.skins.utils.SkinModelCache;

public class PingRenderer {

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(PingRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        if (PingManager.activePings.isEmpty()) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        MinecraftClient client = MinecraftClient.getInstance();
        float tickDelta = context.tickDelta();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        long time = System.currentTimeMillis();

        for (Ping ping : PingManager.activePings) {
            double tx = ping.getPos().x;
            double ty = ping.getPos().y;
            double tz = ping.getPos().z;
            boolean isEntity = ping.getEntityId() != -1;
            String skin = ping.getSkin();
            if (isEntity && client.world != null) {
                Entity e = client.world.getEntityById(ping.getEntityId());
                if (e != null && e.isAlive()) {
                    tx = MathHelper.lerp(tickDelta, e.prevX, e.getX());
                    tz = MathHelper.lerp(tickDelta, e.prevZ, e.getZ());
                    double currentY = MathHelper.lerp(tickDelta, e.prevY, e.getY());
                    ty = currentY + e.getHeight() + 0.5;
                    ping.setPos(new Vec3d(e.getX(), e.getY() + e.getHeight() + 0.5, e.getZ()));
                }
            }

            matrices.push();
            matrices.translate(tx - cameraPos.x, ty - cameraPos.y, tz - cameraPos.z);

            Identifier customTexture = SkinManager.getTexture(skin);
            NativeImage pixelData = SkinManager.getImageData(skin);

            int c = ping.getColor();
            float r = ((c >> 16) & 0xFF) / 255f;
            float g = ((c >> 8) & 0xFF) / 255f;
            float b = ((c) & 0xFF) / 255f;

            if (customTexture != null && pixelData != null) {
                RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
                RenderSystem.setShaderTexture(0, customTexture);
                RenderSystem.enableCull();
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.enableBlend();

                float bobbing = (float) Math.sin(time / 200.0) * 0.1f + 0.2f;
                float rotation = (time / 40.0f) % 360;

                matrices.push();

                if (isEntity) {
                    matrices.translate(0, 1.15 + bobbing, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
                    matrices.scale(2.0f, 2.0f, 2.0f);
                } else {
                    matrices.translate(0, 0.7 + bobbing, 0);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
                    matrices.scale(1.5f, 1.5f, 1.5f);
                }

                SkinModelCache.render(skin, pixelData, matrices);

                matrices.pop();

                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                RenderSystem.disableCull();
                RenderSystem.disableDepthTest();

                float beamStart = isEntity ? (2.13f + bobbing) : (1.5f + bobbing);
                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                drawCylindricalBeam(buffer, matrices.peek().getPositionMatrix(), 0.2f, beamStart, 300.0f, r, g, b, 0.4f);
                tessellator.draw();

                if (!isEntity) {
                    float pulseAlpha = (float) Math.sin(time / 250.0) * 0.2f + 0.6f;
                    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    drawPulsingRing(buffer, matrices.peek().getPositionMatrix(), 0.7f, r, g, b, pulseAlpha);
                    tessellator.draw();
                }

                RenderSystem.enableDepthTest();
                RenderSystem.enableCull();
            }

            else {
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableCull();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);

                if (!isEntity) {
                    float pulseAlpha = (float) Math.sin(time / 250.0) * 0.2f + 0.6f;
                    Matrix4f matrix = matrices.peek().getPositionMatrix();
                    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    drawPulsingRing(buffer, matrix, 0.6f, r, g, b, pulseAlpha);
                    tessellator.draw();
                }

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
            }

            matrices.pop();
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
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

    private static void drawPulsingRing(BufferBuilder buffer, Matrix4f matrix, float radius, float r, float g, float b, float alpha) {

        int segments = 64;
        float innerRadius = 0.1f;

        for (int i = 0; i < segments; i++) {
            double a1 = Math.toRadians((360.0 / segments) * i);
            double a2 = Math.toRadians((360.0 / segments) * (i + 1));

            float x1_in = (float) Math.cos(a1) * innerRadius;
            float z1_in = (float) Math.sin(a1) * innerRadius;
            float x1_out = (float) Math.cos(a1) * radius;
            float z1_out = (float) Math.sin(a1) * radius;

            float x2_in = (float) Math.cos(a2) * innerRadius;
            float z2_in = (float) Math.sin(a2) * innerRadius;
            float x2_out = (float) Math.cos(a2) * radius;
            float z2_out = (float) Math.sin(a2) * radius;

            buffer.vertex(matrix, x1_in, 0.05f, z1_in).color(r, g, b, 0.0f).next();
            buffer.vertex(matrix, x2_in, 0.05f, z2_in).color(r, g, b, 0.0f).next();
            buffer.vertex(matrix, x2_out, 0.05f, z2_out).color(r, g, b, alpha).next();
            buffer.vertex(matrix, x1_out, 0.05f, z1_out).color(r, g, b, alpha).next();
        }
    }
}