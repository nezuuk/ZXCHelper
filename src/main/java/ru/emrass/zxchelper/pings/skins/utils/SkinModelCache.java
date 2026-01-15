package ru.emrass.zxchelper.pings.skins.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class SkinModelCache {

    private static final Map<String, VertexBuffer> cache = new HashMap<>();

    public static void render(String skinName, NativeImage image, MatrixStack matrices) {
        VertexBuffer vbo = cache.get(skinName);

        if (vbo == null) {
            vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
            uploadVoxelGeometry(vbo, image);
            cache.put(skinName, vbo);
        }

        RenderSystem.disableCull();

        vbo.bind();
        vbo.draw(matrices.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();

        RenderSystem.enableCull();
    }

    public static void clear() {
        cache.values().forEach(VertexBuffer::close);
        cache.clear();
    }

    private static void uploadVoxelGeometry(VertexBuffer vbo, NativeImage image) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

        int w = image.getWidth();
        int h = image.getHeight();
        float pxW = 1.0f / w;
        float pxH = 1.0f / h;

        float thickness = 0.0625f;
        float halfThick = thickness / 2.0f;
        float startX = -0.5f;
        float startY = -0.5f;

        float eps = 0.0001f;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (isTransp(image, x, y)) continue;

                float x1 = startX + (x * pxW);
                float x2 = startX + ((x + 1) * pxW);
                float y1 = startY + ((h - 1 - y) * pxH);
                float y2 = startY + ((h - y) * pxH);

                float u = (x + 0.5f) * pxW;
                float v = (y + 0.5f) * pxH;

                vertex(buffer, x1, y1, halfThick, u, v);
                vertex(buffer, x2, y1, halfThick, u, v);
                vertex(buffer, x2, y2, halfThick, u, v);
                vertex(buffer, x1, y2, halfThick, u, v);

                vertex(buffer, x2, y1, -halfThick, u, v);
                vertex(buffer, x1, y1, -halfThick, u, v);
                vertex(buffer, x1, y2, -halfThick, u, v);
                vertex(buffer, x2, y2, -halfThick, u, v);

                if (x == 0 || isTransp(image, x - 1, y))
                    side(buffer, x1, y1-eps, x1, y2+eps, -halfThick, halfThick, u, v);

                if (x == w - 1 || isTransp(image, x + 1, y))
                    side(buffer, x2, y1-eps, x2, y2+eps, halfThick, -halfThick, u, v);

                if (y == 0 || isTransp(image, x, y - 1))
                    side(buffer, x1-eps, y2, x2+eps, y2, halfThick, -halfThick, u, v);

                if (y == h - 1 || isTransp(image, x, y + 1))
                    side(buffer, x1-eps, y1, x2+eps, y1, -halfThick, halfThick, u, v);
            }
        }

        BufferBuilder.BuiltBuffer builtBuffer = buffer.end();
        vbo.bind();
        vbo.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    private static boolean isTransp(NativeImage img, int x, int y) {
        return ((img.getColor(x, y) >> 24) & 0xFF) < 10;
    }

    private static void vertex(BufferBuilder b, float x, float y, float z, float u, float v) {
        b.vertex(x, y, z).color(1f, 1f, 1f, 1f).texture(u, v).next();
    }

    private static void side(BufferBuilder b, float x1, float y1, float x2, float y2, float z1, float z2, float u, float v) {
        b.vertex(x1, y1, z1).color(1f, 1f, 1f, 1f).texture(u, v).next();
        b.vertex(x2, y2, z1).color(1f, 1f, 1f, 1f).texture(u, v).next();
        b.vertex(x2, y2, z2).color(1f, 1f, 1f, 1f).texture(u, v).next();
        b.vertex(x1, y1, z2).color(1f, 1f, 1f, 1f).texture(u, v).next();
    }
}