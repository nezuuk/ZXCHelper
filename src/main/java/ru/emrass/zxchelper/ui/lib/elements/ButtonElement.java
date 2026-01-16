package ru.emrass.zxchelper.ui.lib.elements;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ui.lib.ConfigElement;
import ru.emrass.zxchelper.ui.lib.ZxcTheme;

public class ButtonElement extends ConfigElement<Void> {

    private final Runnable onClick;

    public ButtonElement(Text label, Runnable onClick) {
        super(label, null);
        this.onClick = onClick;
    }

    @Override
    public Component createComponent() {

        FlowLayout container = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        container.margins(Insets.bottom(5));

        ButtonComponent button = Components.button(label, btn -> onClick.run());

        button.renderer(ButtonComponent.Renderer.flat(
                0xFF444444, ZxcTheme.PRIMARY.argb(), 0xFF222222
        ));
        button.sizing(Sizing.fill(100), Sizing.fixed(25));

        if (tooltip != null) button.tooltip(tooltip);

        container.child(button);
        return container;
    }
}