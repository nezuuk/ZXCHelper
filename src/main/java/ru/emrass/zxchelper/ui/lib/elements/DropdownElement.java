package ru.emrass.zxchelper.ui.lib.elements;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
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

public class DropdownElement extends ConfigElement<String> {

    private final List<String> options;
    private boolean expanded = false;

    public DropdownElement(Text label, String initialValue, List<String> options) {
        super(label, initialValue);
        this.options = options;
    }

    @Override
    public Component createComponent() {

        FlowLayout root = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        root.margins(Insets.bottom(10));

        root.child(Components.label(label)
                .color(ZxcTheme.TEXT_DIM).margins(Insets.bottom(2)));

        FlowLayout listContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(0));
        listContainer.allowOverflow(false);
        listContainer.surface(Surface.flat(0xFF111111).and(Surface.outline(ZxcTheme.PRIMARY.argb())));

        ButtonComponent mainBtn = Components.button(Text.literal(value + " ▼"), btn -> {
            expanded = !expanded;
            if (expanded) {
                btn.setMessage(Text.literal(value + " ▲"));

                listContainer.clearChildren();
                for (String opt : options) {
                    var optBtn = Components.button(Text.literal(opt), b -> {
                        this.value = opt;
                        btn.setMessage(Text.literal(opt + " ▼"));
                        expanded = false;
                        listContainer.sizing(Sizing.fill(100), Sizing.fixed(0));
                        if (onGlobalUpdate != null) onGlobalUpdate.run();
                    });

                    optBtn.sizing(Sizing.fill(100), Sizing.fixed(20));
                    optBtn.renderer(ButtonComponent.Renderer.flat(0x00000000, 0x409932CC, 0x40FFFFFF));
                    listContainer.child(optBtn);
                }
                listContainer.sizing(Sizing.fill(100), Sizing.content());
            } else {
                btn.setMessage(Text.literal(value + " ▼"));
                listContainer.clearChildren();
                listContainer.sizing(Sizing.fill(100), Sizing.fixed(0));
            }
        });

        mainBtn.sizing(Sizing.fill(100), Sizing.fixed(25));
        mainBtn.renderer(ButtonComponent.Renderer.flat(0xFF202020, 0xFF303030, 0xFF404040));

        root.child(mainBtn);
        root.child(listContainer);

        return root;
    }
}