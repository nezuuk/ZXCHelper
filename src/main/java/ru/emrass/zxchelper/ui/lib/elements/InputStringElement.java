package ru.emrass.zxchelper.ui.lib.elements;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ui.lib.ConfigElement;
import ru.emrass.zxchelper.ui.lib.ZxcTheme;

import java.util.List;

public class InputStringElement extends ConfigElement<String> {

    private List<String> suggestions = null;

    public InputStringElement(Text label, String initialValue) {
        super(label, initialValue);
    }

    public InputStringElement setSuggestions(List<String> list) {
        this.suggestions = list;
        return this;
    }

    @Override
    public Component createComponent() {
        this.labelComponent = Components.label(label);
        updateErrorState();
        FlowLayout container = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        container.margins(Insets.bottom(10));

        var title = labelComponent;
        title.color(ZxcTheme.TEXT_MAIN).margins(Insets.bottom(3));
        if (tooltip != null) title.tooltip(tooltip);
        container.child(title);

        FlowLayout inputWrapper = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        inputWrapper.padding(Insets.of(2));
        inputWrapper.surface(Surface.flat(0xFF151515).and(Surface.outline(0xFF404040)));

        TextBoxComponent input = Components.textBox(Sizing.fill(100));
        input.setText(value);
        input.setMaxLength(512);

        inputWrapper.child(input);
        container.child(inputWrapper);
        input.text(value);
        FlowLayout suggestionsBox = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        suggestionsBox.margins(Insets.top(2));


        input.onChanged().subscribe(val -> {

            this.value = val;
            updateErrorState();
            if (onGlobalUpdate != null) onGlobalUpdate.run();

            suggestionsBox.clearChildren();
            if (suggestions != null) {
                int count = 0;
                for (String s : suggestions) {
                    if (s.toLowerCase().startsWith(val.toLowerCase()) && !s.equalsIgnoreCase(val)) {

                        var btn = Components.button(Text.literal(s), b -> {
                            input.setText(s);
                            this.value = s;
                            suggestionsBox.clearChildren();
                            inputWrapper.surface(Surface.flat(0xFF151515).and(Surface.outline(0xFF404040)));

                            if (onGlobalUpdate != null) onGlobalUpdate.run();
                        });

                        btn.sizing(Sizing.fill(100), Sizing.fixed(15));
                        btn.renderer(ButtonComponent.Renderer.flat(0xFF252525, 0xFF353535, 0xFF151515));
                        btn.margins(Insets.bottom(1));

                        suggestionsBox.child(btn);

                        count++;
                        if (count >= 3) break;
                    }
                }
            }
        });

        container.child(suggestionsBox);

        return container;
    }
}