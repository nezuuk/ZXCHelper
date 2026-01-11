package ru.emrass.zxchelper.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;

public class ChatWheelScreen extends Screen {

    private final int activationKeyCode;
    private final List<String> messages;
    private int selectedIndex = -1;

    public ChatWheelScreen(int activationKeyCode) {
        super(Text.literal("Chat Wheel"));
        this.activationKeyCode = activationKeyCode;
        this.messages = ConfigManager.getConfig().getChatWheelMessages();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        if (!InputUtil.isKeyPressed(windowHandle, activationKeyCode)) {
            sendMessageAndClose();
            return;
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        double radiusOuter = 100;
        double radiusInner = 30;

        int itemCount = messages.size();
        if (itemCount == 0) return;

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

        for (int i = 0; i < itemCount; i++) {
            boolean isSelected = (i == selectedIndex);

            double startAngle = (i * angleStep) - 90 - (angleStep / 2);
            double endAngle = startAngle + angleStep;

            int color = isSelected ? 0xAAFFD700 : 0x90000000;
            drawSector(context, centerX, centerY, radiusInner, radiusOuter, startAngle, endAngle, color);
        }


        for (int i = 0; i < itemCount; i++) {
            double lineAngle = (i * angleStep) - 90 - (angleStep / 2);
            drawLine(context, centerX, centerY, radiusInner, radiusOuter, lineAngle, 0xFF1164B4);
        }

        for (int i = 0; i < itemCount; i++) {
            String msg = messages.get(i);

            double textAngle = (i * angleStep) - 90;
            double textRad = Math.toRadians(textAngle);
            double textDist = (radiusInner + radiusOuter) / 2;

            int tx = (int) (centerX + Math.cos(textRad) * textDist);
            int ty = (int) (centerY + Math.sin(textRad) * textDist);

            int color = (i == selectedIndex) ? 0xFFFF00 : 0xFFFFFF;

            int maxWidth = 75;
            List<OrderedText> lines = this.textRenderer.wrapLines(Text.literal(msg), maxWidth);
            int totalHeight = lines.size() * this.textRenderer.fontHeight;
            int currentY = ty - (totalHeight / 2);

            for (OrderedText line : lines) {
                int lineWidth = this.textRenderer.getWidth(line);
                context.drawTextWithShadow(this.textRenderer, line, tx - (lineWidth / 2), currentY, color);
                currentY += this.textRenderer.fontHeight;
            }
        }
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

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            sendMessageAndClose();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void sendMessageAndClose() {
        if (selectedIndex >= 0 && selectedIndex < messages.size()) {
            String msg = messages.get(selectedIndex);
            if (this.client != null && this.client.player != null) {
                this.client.getNetworkHandler().sendChatMessage(msg);
            }
        }
        this.close();
    }
}