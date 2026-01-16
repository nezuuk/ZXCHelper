package ru.emrass.zxchelper.ui.lib;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;

public class ConfirmExitModal {

    public static Component create(Runnable onStay, Runnable onExit) {
        FlowLayout overlay = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));
        overlay.surface(Surface.flat(0xCC000000));
        overlay.horizontalAlignment(HorizontalAlignment.CENTER);
        overlay.verticalAlignment(VerticalAlignment.CENTER);

        FlowLayout dialog = Containers.verticalFlow(Sizing.fixed(200), Sizing.content());
        dialog.surface(Surface.flat(0xFF101010)
                .and(Surface.outline(0xFF9932CC)));

        dialog.padding(Insets.of(15));
        dialog.horizontalAlignment(HorizontalAlignment.CENTER);

        dialog.child(Components.label(Text.literal("ВЫЙТИ БЕЗ СОХРАНЕНИЯ?").formatted(net.minecraft.util.Formatting.RED, net.minecraft.util.Formatting.BOLD))
                .margins(Insets.bottom(10)));
        dialog.child(Components.label(Text.literal("В настройках есть ошибки."))
                .color(Color.ofRgb(0xAAAAAA)).margins(Insets.bottom(15)));

        FlowLayout buttons = Containers.horizontalFlow(Sizing.content(), Sizing.content());

        ButtonComponent btnStay = Components.button(Text.literal("ОСТАТЬСЯ"), b -> onStay.run());
        btnStay.sizing(Sizing.fixed(80), Sizing.fixed(20));
        btnStay.margins(Insets.right(10));
        btnStay.renderer(ButtonComponent.Renderer.flat(0xFF228822, 0xFF44AA44, 0xFF116611));

        ButtonComponent btnExit = Components.button(Text.literal("ВЫЙТИ"), b -> onExit.run());
        btnExit.sizing(Sizing.fixed(80), Sizing.fixed(20));
        btnExit.renderer(ButtonComponent.Renderer.flat(0xFF552222, 0xFFFF0000, 0xFF330000));

        buttons.child(btnStay);
        buttons.child(btnExit);
        dialog.child(buttons);

        overlay.child(dialog);
        return overlay;
    }
}