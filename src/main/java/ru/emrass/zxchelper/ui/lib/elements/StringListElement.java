package ru.emrass.zxchelper.ui.lib.elements;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import ru.emrass.zxchelper.ui.lib.ConfigElement;
import ru.emrass.zxchelper.ui.lib.ZxcTheme;

import java.util.ArrayList;
import java.util.List;

public class StringListElement extends ConfigElement<List<String>> {

    private FlowLayout entriesContainer;
    private ButtonComponent expandBtn;
    private ButtonComponent addBtn;
    private boolean expanded = false;

    public StringListElement(Text label, List<String> initialValue) {
        super(label, new ArrayList<>(initialValue));
    }

    @Override
    public Component createComponent() {
        FlowLayout root = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        root.margins(Insets.bottom(10));

        FlowLayout header = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(24));
        header.verticalAlignment(VerticalAlignment.CENTER);
        header.margins(Insets.bottom(5));

        header.mouseDown().subscribe((mouseX, mouseY, button) -> {
            toggleExpand();
            MinecraftClient.getInstance().getSoundManager().play(
                    net.minecraft.client.sound.PositionedSoundInstance.master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        });

        expandBtn = Components.button(Text.literal(expanded ? "▼" : "▶"), btn -> toggleExpand());
        expandBtn.sizing(Sizing.fixed(20), Sizing.fixed(20));
        expandBtn.renderer(ButtonComponent.Renderer.flat(0x00000000, 0x40FFFFFF, 0x20FFFFFF));
        header.child(expandBtn);

        this.labelComponent = Components.label(label);
        labelComponent.color(ZxcTheme.TEXT_MAIN);
        labelComponent.margins(Insets.left(5));
        header.child(labelComponent);

        header.child(Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(1)));

        addBtn = Components.button(Text.literal("➕"), btn -> {
            this.value.add("");
            refreshEntries();
            updateErrorState();
            if (onGlobalUpdate != null) onGlobalUpdate.run();
        });
        addBtn.sizing(Sizing.fixed(20), Sizing.fixed(20));
        addBtn.renderer(ButtonComponent.Renderer.flat(0xFF225522, 0xFF44AA44, 0xFF113311));
        addBtn.positioning(Positioning.relative(100, 50));
        if (!expanded) addBtn.sizing(Sizing.fixed(0), Sizing.fixed(0));

        header.child(addBtn);
        root.child(header);

        entriesContainer = Containers.verticalFlow(Sizing.fill(100), expanded ? Sizing.content() : Sizing.fixed(0));
        entriesContainer.allowOverflow(false);
        entriesContainer.padding(Insets.left(10));

        refreshEntries();
        root.child(entriesContainer);
        updateErrorState();

        return root;
    }

    private void toggleExpand() {
        expanded = !expanded;
        if (expanded) {
            expandBtn.setMessage(Text.literal("▼"));
            entriesContainer.sizing(Sizing.fill(100), Sizing.content());
            addBtn.sizing(Sizing.fixed(20), Sizing.fixed(20));
        } else {
            expandBtn.setMessage(Text.literal("▶"));
            entriesContainer.sizing(Sizing.fill(100), Sizing.fixed(0));
            addBtn.sizing(Sizing.fixed(0), Sizing.fixed(0));
        }
    }

    private void refreshEntries() {
        entriesContainer.clearChildren();

        for (int i = 0; i < value.size(); i++) {
            int index = i;
            String str = value.get(i);

            FlowLayout row = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
            row.margins(Insets.bottom(4));

            FlowLayout inputWrapper = Containers.verticalFlow(Sizing.fill(92), Sizing.content());
            inputWrapper.padding(Insets.of(2));
            inputWrapper.surface(Surface.flat(0xFF151515).and(Surface.outline(0xFF404040)));

            TextBoxComponent input = Components.textBox(Sizing.fill(100), str);
            input.setMaxLength(256);
            input.setEditableColor(0xFFE0E0E0);
            input.setCursor(0);

            input.onChanged().subscribe(newVal -> {
                this.value.set(index, newVal);
                updateErrorState();
                if (onGlobalUpdate != null) onGlobalUpdate.run();
            });

            inputWrapper.child(input);
            row.child(inputWrapper);

            ButtonComponent delBtn = Components.button(Text.literal("✖"), btn -> {
                this.value.remove(index);
                refreshEntries();
                updateErrorState();
                if (onGlobalUpdate != null) onGlobalUpdate.run();
            });

            delBtn.sizing(Sizing.fixed(20), Sizing.fixed(20));
            delBtn.renderer(ButtonComponent.Renderer.flat(0xFF552222, 0xFFFF0000, 0xFF330000));

            delBtn.positioning(Positioning.relative(100, 50));

            row.child(delBtn);
            entriesContainer.child(row);
        }
    }
}