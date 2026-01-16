package ru.emrass.zxchelper.ui.lib;

import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZxcBuilder {
    private final Screen parent;

    private final Map<String, List<ConfigElement<?>>> categories = new LinkedHashMap<>();

    public ZxcBuilder(Screen parent) {
        this.parent = parent;
    }

    public CategoryBuilder category(String name) {
        categories.putIfAbsent(name, new ArrayList<>());
        return new CategoryBuilder(name);
    }

    public Screen build() {
        return new ZxcGeneratedScreen(parent, categories);
    }

    public class CategoryBuilder {
        private final String categoryName;

        public CategoryBuilder(String categoryName) {
            this.categoryName = categoryName;
        }

        public CategoryBuilder addEntry(ConfigElement<?> element) {
            if (element != null) {
                categories.get(categoryName).add(element);
            }
            return this;
        }
    }
}