package ru.emrass.zxchelper.ui.lib.elements;

import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ui.lib.ConfigElement;
import ru.emrass.zxchelper.ui.lib.ZxcTheme;

import java.util.function.Consumer;

public class ColorElement extends ConfigElement<Integer> {

    public ColorElement(Text label, Integer initialValue, Consumer<Integer> saveConsumer) {
        super(label, initialValue);
        this.saveConsumer = saveConsumer;
    }

    @Override
    public Component createComponent() {

        FlowLayout container = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        container.margins(Insets.bottom(15));

        container.child(Components.label(label).color(ZxcTheme.TEXT_MAIN));

        FlowLayout row = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);

        BoxComponent previewBox = Components.box(Sizing.fixed(40), Sizing.fixed(40));
        previewBox.fill(true);
        previewBox.color(Color.ofArgb(0xFF000000 | value));
        FlowLayout previewWrapper = Containers.verticalFlow(Sizing.content(), Sizing.content());
        previewWrapper.surface(Surface.outline(0xFFFFFFFF));
        previewWrapper.child(previewBox);

        row.child(previewWrapper);

        row.child(Containers.horizontalFlow(Sizing.fixed(10), Sizing.fixed(1)));

        FlowLayout sliders = Containers.verticalFlow(Sizing.fill(70), Sizing.content());

        int r = (value >> 16) & 0xFF;
        int g = (value >> 8) & 0xFF;
        int b = value & 0xFF;

        sliders.child(createSlider("R", r, 0xFFFF5555, val -> {
            int newR = val;
            int newG = (this.value >> 8) & 0xFF;
            int newB = this.value & 0xFF;
            updateValue(newR, newG, newB, previewBox);
        }));

        sliders.child(createSlider("G", g, 0xFF55FF55, val -> {
            int newR = (this.value >> 16) & 0xFF;
            int newG = val;
            int newB = this.value & 0xFF;
            updateValue(newR, newG, newB, previewBox);
        }));

        sliders.child(createSlider("B", b, 0xFF5555FF, val -> {
            int newR = (this.value >> 16) & 0xFF;
            int newG = (this.value >> 8) & 0xFF;
            int newB = val;
            updateValue(newR, newG, newB, previewBox);
        }));

        row.child(sliders);
        container.child(row);
        return container;
    }

    private void updateValue(int r, int g, int b, BoxComponent preview) {
        this.value = (r << 16) | (g << 8) | b;
        preview.color(Color.ofArgb(0xFF000000 | this.value));

        if (onGlobalUpdate != null) onGlobalUpdate.run();
    }

    private Component createSlider(String name, int initial, int color, Consumer<Integer> onChange) {

        var slider = Components.discreteSlider(Sizing.fill(100), 0, 255);
        slider.setTooltip(Tooltip.of(Text.literal(name)));
        slider.setFromDiscreteValue(initial);
        slider.onChanged().subscribe(val -> onChange.accept((int) val));


        return slider;
    }
}