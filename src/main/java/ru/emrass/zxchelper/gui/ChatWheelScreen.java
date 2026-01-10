package ru.emrass.zxchelper.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.emrass.zxchelper.config.ConfigManager;

import java.util.List;

public class ChatWheelScreen extends Screen {

    private final int keyBindingCode;
    private int selectedIndex = -1;
    private final List<String> messages;

    public ChatWheelScreen(int keyBindingCode) {
        super(Text.literal("Chat Wheel"));
        this.keyBindingCode = keyBindingCode;
        this.messages = ConfigManager.getConfig().getChatWheelMessages();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        double radiusOuter = 100;
        double radiusInner = 30;

        int itemCount = messages.size();
        if (itemCount == 0) return;

        double angleStep = 360.0 / itemCount;

        // 1. Математика выбора сектора
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Считаем угол. -90 сдвиг, чтобы 0 был сверху
        double rawAngle = Math.toDegrees(Math.atan2(dy, dx));
        double correctedAngle = rawAngle + 90 + (angleStep / 2);
        if (correctedAngle < 0) correctedAngle += 360;

        if (dist >= radiusInner) {
            this.selectedIndex = (int) (correctedAngle / angleStep) % itemCount;
        } else {
            this.selectedIndex = -1;
        }

        // 2. Рисуем сектора
        for (int i = 0; i < itemCount; i++) {
            boolean isSelected = (i == selectedIndex);

            double startAngle = (i * angleStep) - 90 - (angleStep / 2);
            double endAngle = startAngle + angleStep;

            // Цвет: Формат ARGB. 0x80000000 - полупрозрачный черный, 0x80FFFFFF - полупрозрачный белый
            int color = isSelected ? 0x90FFFFFF : 0x90000000;

            drawSector(context, centerX, centerY, radiusInner, radiusOuter, startAngle, endAngle, color);
        }

        // 3. Рисуем текст
        for (int i = 0; i < itemCount; i++) {
            String msg = messages.get(i);

            // Угол текста - середина сектора
            double textAngle = (i * angleStep) - 90;
            double textRad = Math.toRadians(textAngle);
            double textDist = (radiusInner + radiusOuter) / 2; // Посередине бублика

            int tx = (int) (centerX + Math.cos(textRad) * textDist);
            int ty = (int) (centerY + Math.sin(textRad) * textDist);

            int textColor = (i == selectedIndex) ? 0xFFFF00 : 0xFFFFFF; // Желтый если выбран
            context.drawCenteredTextWithShadow(this.textRenderer, msg, tx, ty - 4, textColor);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    // Вспомогательный метод для рисования части круга
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

        int segments = 10; // Чем больше, тем плавнее круг
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
    public boolean shouldPause() { return false; }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Если отпустили кнопку открытия меню - отправляем сообщение
        if (keyCode == this.keyBindingCode) {
            sendMessageAndClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Если кликнули мышкой - тоже отправляем
        if (button == 0) {
            sendMessageAndClose();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void sendMessageAndClose() {
        if (selectedIndex >= 0 && selectedIndex < messages.size()) {
            String msg = messages.get(selectedIndex);
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(msg);
            }
        }
        this.close();
    }
}