package ru.emrass.zxchelper.ui.lib;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;

public class ZxcGeneratedScreen extends BaseOwoScreen<FlowLayout> {

    private final Screen parent;
    private final Map<String, List<ConfigElement<?>>> categories;

    private FlowLayout contentArea;
    private FlowLayout tabsArea;
    private ButtonComponent saveButton;
    private String currentCategory = "";
    private boolean canExit = true;
    private Component modal = null;

    public ZxcGeneratedScreen(Screen parent, Map<String, List<ConfigElement<?>>> categories) {
        this.parent = parent;
        this.categories = categories;
        if (!categories.isEmpty()) {
            this.currentCategory = categories.keySet().iterator().next();
        }
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout root) {
        root.id("root");
        root.surface(Surface.blur(15, 15))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);
        FlowLayout window = Containers.verticalFlow(Sizing.fill(85), Sizing.fill(85));
        window.surface(Surface.flat(0xFF090909).and(Surface.outline(ZxcTheme.PRIMARY.argb())));

        window.allowOverflow(false);

        FlowLayout header = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(40));
        header.padding(Insets.of(10));
        header.verticalAlignment(VerticalAlignment.CENTER);
        header.surface(Surface.flat(0xFF151515));

        header.child(Components.label(Text.literal("ZXC CONFIG").formatted(net.minecraft.util.Formatting.BOLD))
                .shadow(true).color(ZxcTheme.PRIMARY).margins(Insets.right(20)));

        tabsArea = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        for (String catName : categories.keySet()) {
            var btn = Components.button(Text.literal(catName), b -> loadCategory(catName));
            btn.margins(Insets.horizontal(2));
            btn.sizing(Sizing.content(), Sizing.fixed(20));
            tabsArea.child(btn);
        }
        updateTabsStyle();
        header.child(tabsArea);
        window.child(header);

        ScrollContainer<FlowLayout> scroll = Containers.verticalScroll(
                Sizing.fill(100), Sizing.fill(100),
                Containers.verticalFlow(Sizing.fill(100), Sizing.content())
        );
        this.contentArea = scroll.child();
        this.contentArea.padding(Insets.of(20));

        window.child(scroll);

        FlowLayout footer = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(50));
        footer.padding(Insets.of(10));
        footer.horizontalAlignment(HorizontalAlignment.RIGHT);
        footer.verticalAlignment(VerticalAlignment.CENTER);
        footer.surface(Surface.flat(0xFF151515));
        footer.zIndex(100);

        footer.positioning(Positioning.relative(0, 100));

        this.saveButton = Components.button(Text.literal("СОХРАНИТЬ"), btn -> {
            if (canExit) {
                saveAll();
                this.close();
            } else {
                btn.setMessage(Text.literal("ИСПРАВЬ ОШИБКИ!"));
            }
        });
        saveButton.sizing(Sizing.fixed(120), Sizing.fixed(30));
        saveButton.renderer(ButtonComponent.Renderer.flat(ZxcTheme.SUCCESS.argb(), 0xFF88FF88, 0xFF228822));

        footer.child(saveButton);
        window.child(footer);

        scroll.margins(Insets.bottom(50));

        root.child(window);

        loadCategory(currentCategory);
        validateAll();
    }


    private void loadCategory(String name) {
        this.currentCategory = name;
        updateTabsStyle();
        contentArea.clearChildren();
        List<ConfigElement<?>> elements = categories.get(name);
        if (elements != null) {
            for (ConfigElement<?> el : elements) {
                el.setGlobalUpdateListener(this::validateAll);
                contentArea.child(el.createComponent());
            }
        }
    }

    private void updateTabsStyle() {
        if (tabsArea == null) return;
        for (var child : tabsArea.children()) {
            if (child instanceof ButtonComponent btn) {
                boolean isActive = btn.getMessage().getString().equals(currentCategory);
                if (isActive) {
                    btn.renderer(ButtonComponent.Renderer.flat(ZxcTheme.PRIMARY.argb(), ZxcTheme.ACCENT.argb(), ZxcTheme.PRIMARY.argb()));
                } else {
                    btn.renderer(ButtonComponent.Renderer.flat(0x00000000, 0x40FFFFFF, 0x20FFFFFF));
                }
            }
        }
    }

    private void validateAll() {
        boolean hasErrors = false;
        for (List<ConfigElement<?>> list : categories.values()) {
            for (ConfigElement<?> el : list) {
                if (el.getError().isPresent()) {
                    hasErrors = true;
                    break;
                }
            }
        }
        this.canExit = !hasErrors;
        saveButton.active = !hasErrors;
        if (hasErrors) {
            saveButton.setMessage(Text.literal("ОШИБКА"));
            saveButton.renderer(ButtonComponent.Renderer.flat(ZxcTheme.ERROR.argb(), ZxcTheme.ERROR.argb(), ZxcTheme.ERROR.argb()));
        } else {
            saveButton.setMessage(Text.literal("СОХРАНИТЬ"));
            saveButton.renderer(ButtonComponent.Renderer.flat(ZxcTheme.SUCCESS.argb(), 0xFF88FF88, 0xFF228822));
        }
    }

    private void saveAll() {
        for (List<ConfigElement<?>> list : categories.values()) {
            for (ConfigElement<?> el : list) {
                el.save();
            }
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {

            if (modal != null) {
                removeModal();
                return true;
            }

            if (canExit) {
                saveAll();
                this.close();
            } else {
                showConfirmDialog();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void showConfirmDialog() {
        this.modal = ConfirmExitModal.create(
                this::removeModal,
                this::close
        );

        this.modal.positioning(Positioning.absolute(0, 0));

        this.modal.zIndex(100);

        (this.uiAdapter.rootComponent).child(modal);
    }

    private void removeModal() {
        if (modal != null) {
            (this.uiAdapter.rootComponent).removeChild(modal);
            modal = null;
        }
    }
}