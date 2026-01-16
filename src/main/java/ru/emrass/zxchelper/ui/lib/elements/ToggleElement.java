package ru.emrass.zxchelper.ui.lib.elements;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ui.lib.ConfigElement;
import ru.emrass.zxchelper.ui.lib.ZxcTheme;

import java.util.function.Consumer;

public class ToggleElement extends ConfigElement<Boolean> {

    public ToggleElement(Text label, Boolean initialValue, Consumer<Boolean> saveConsumer) {
        super(label, initialValue);
        if (saveConsumer != null) this.saveConsumer = saveConsumer;
    }

    @Override
    public Component createComponent() {


        FlowLayout container = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(30));
        container.verticalAlignment(VerticalAlignment.CENTER);
        container.margins(Insets.bottom(4));


        var labelComp = Components.label(label).color(ZxcTheme.TEXT_MAIN);
        labelComp.horizontalSizing(Sizing.fill(70));
        container.child(labelComp);

        var btn = Components.button(getText(), button -> {
            this.value = !this.value;
            button.setMessage(getText());
            updateStyle(button);
            if (onGlobalUpdate != null) onGlobalUpdate.run();
        });

        btn.sizing(Sizing.fixed(60), Sizing.fixed(20));
        btn.positioning(Positioning.relative(100, 50));

        updateStyle(btn);


        container.child(btn);
        return container;
    }

    private Text getText() {
        return Text.literal(value ? "ВКЛ" : "ВЫКЛ").formatted(net.minecraft.util.Formatting.BOLD);
    }

    private void updateStyle(ButtonComponent btn) {
        if (value) {
            btn.renderer(ButtonComponent.Renderer.flat(0xFF228822, 0xFF44AA44, 0xFF116611));
        } else {
            btn.renderer(ButtonComponent.Renderer.flat(0xFF882222, 0xFFAA4444, 0xFF661111));
        }
    }
}