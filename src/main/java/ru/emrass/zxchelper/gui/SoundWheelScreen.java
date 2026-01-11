package ru.emrass.zxchelper.gui;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.config.ConfigManager;
import ru.emrass.zxchelper.net.WsMessageType;
import ru.emrass.zxchelper.utils.SoundUtils;

import java.util.List;

public class SoundWheelScreen extends Screen {

    private final int activationKeyCode;
    private final List<String> sounds;
    private int selectedIndex = -1;

    public SoundWheelScreen(int activationKeyCode) {
        super(Text.literal("Sound Wheel"));
        this.activationKeyCode = activationKeyCode;
        this.sounds = ConfigManager.getConfig().getSoundWheelSounds();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        if (!InputUtil.isKeyPressed(windowHandle, activationKeyCode)) {
            sendSoundAndClose();
            return;
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        double radiusOuter = 100;
        double radiusInner = 30;

        int itemCount = sounds.size();

        if (itemCount == 0) {
            context.drawCenteredTextWithShadow(this.textRenderer, "§cСписок звуков пуст!", centerX, centerY - 10, 0xFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, "§7Добавь их в настройках мода", centerX, centerY, 0xFFFFFF);
            return;
        }

        double angleStep = 360.0 / itemCount;

        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        double rawAngle = Math.toDegrees(Math.atan2(dy, dx));
        double correctedAngle = rawAngle + 90 + (angleStep / 2);
        if (correctedAngle < 0) correctedAngle += 360;

        if (dist >= radiusInner) {
            this.selectedIndex = (int) (correctedAngle / angleStep) % itemCount;
        } else {
            this.selectedIndex = -1;
        }


        int purpleBorderColor = 0xFF9932CC;
        int blackLineColor = 0xFF000000;

        for (int i = 0; i < itemCount; i++) {
            boolean isSelected = (i == selectedIndex);
            double startAngle = (i * angleStep) - 90 - (angleStep / 2);
            double endAngle = startAngle + angleStep;

            int color = isSelected ? 0xAA9932CC : 0x902E004E;

            drawSector(context, centerX, centerY, radiusInner, radiusOuter, startAngle, endAngle, color);

            drawLine(context, centerX, centerY, radiusInner, radiusOuter, startAngle, blackLineColor);
        }

        drawRing(context, centerX, centerY, (float)radiusInner, 1.2f, blackLineColor);

        for (int i = 0; i < itemCount; i++) {
            String soundName = sounds.get(i);

            boolean exists = SoundUtils.loadedSoundNames.contains(soundName);

            int textColor = 0xFFFFFF;
            if (i == selectedIndex) textColor = 0xFFFF00;
            else if (!exists) textColor = 0xFF5555;

            String displayName = soundName;
            if (displayName.length() > 14) displayName = displayName.substring(0, 12) + "..";

            double textAngle = (i * angleStep) - 90;
            double textRad = Math.toRadians(textAngle);
            double textDist = (radiusInner + radiusOuter) / 2;

            int tx = (int) (centerX + Math.cos(textRad) * textDist);
            int ty = (int) (centerY + Math.sin(textRad) * textDist);

            context.drawCenteredTextWithShadow(this.textRenderer, displayName, tx, ty - 4, textColor);
        }
    }

    private void sendSoundAndClose() {
        if (selectedIndex >= 0 && selectedIndex < sounds.size()) {
            String soundId = sounds.get(selectedIndex);

            if (SoundUtils.loadedSoundNames.contains(soundId)) {
                if (this.client != null && this.client.player != null) {
                    Vec3d pos = this.client.player.getPos();
                    JsonObject json = new JsonObject();
                    json.addProperty("id", soundId);
                    json.addProperty("x", pos.x);
                    json.addProperty("y", pos.y);
                    json.addProperty("z", pos.z);
                    ZXCHelper.getInstance().getWebService().sendJson(WsMessageType.PLAY_SOUND, json);
                }
            } else {
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(Text.literal("Файл '" + soundId + "' не найден!"), true);
                }
            }
        }
        this.close();
    }

    private void drawSector(DrawContext context, int cx, int cy, double rInner, double rOuter, double startAngle, double endAngle, int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        int segments = 10;
        double step = (endAngle - startAngle) / segments;

        for (int i = 0; i <= segments; i++) {
            double angle = Math.toRadians(startAngle + i * step);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            buffer.vertex(matrix, cx + cos * (float)rOuter, cy + sin * (float)rOuter, 0).color(r, g, b, a).next();
            buffer.vertex(matrix, cx + cos * (float)rInner, cy + sin * (float)rInner, 0).color(r, g, b, a).next();
        }
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    private void drawLine(DrawContext context, int cx, int cy, double rInner, double rOuter, double angleDegrees, int color) {
        float thickness = 1.2f;
        float halfWidth = thickness / 2.0f;

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        double angleRad = Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);
        float offsetX = -sin * halfWidth;
        float offsetY = cos * halfWidth;

        float xInner = cx + cos * (float) rInner;
        float yInner = cy + sin * (float) rInner;
        float xOuter = cx + cos * (float) rOuter;
        float yOuter = cy + sin * (float) rOuter;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, xOuter + offsetX, yOuter + offsetY, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, xOuter - offsetX, yOuter - offsetY, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, xInner + offsetX, yInner + offsetY, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, xInner - offsetX, yInner - offsetY, 0).color(r, g, b, a).next();
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    private void drawRing(DrawContext context, int centerX, int centerY, float radius, float thickness, int color) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        float rIn = radius - (thickness / 2);
        float rOut = radius + (thickness / 2);
        int segments = 72;

        for (int i = 0; i <= segments; i++) {
            double angle = Math.toRadians((360.0 / segments) * i);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            buffer.vertex(matrix, centerX + cos * rOut, centerY + sin * rOut, 0).color(r, g, b, a).next();
            buffer.vertex(matrix, centerX + cos * rIn, centerY + sin * rIn, 0).color(r, g, b, a).next();
        }
        tessellator.draw();
        RenderSystem.disableBlend();
    }
}