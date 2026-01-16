package ru.emrass.zxchelper.ui.lib;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Component;
import lombok.Getter;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ConfigElement<T> {
    protected final Text label;
    @Getter
    protected T value;
    protected Consumer<T> saveConsumer = v -> {};
    protected Function<T, Optional<Text>> errorSupplier = v -> Optional.empty();
    protected T defaultValue;
    protected Text tooltip;
    protected Runnable onGlobalUpdate;
    protected LabelComponent labelComponent;

    public ConfigElement(Text label, T initialValue) {
        this.label = label;
        this.value = initialValue;
    }

    public ConfigElement<T> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public ConfigElement<T> setErrorSupplier(Function<T, Optional<Text>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }

    public ConfigElement<T> setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConfigElement<T> setTooltip(Text tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public ConfigElement<T> build() {
        return this;
    }

    public void setGlobalUpdateListener(Runnable listener) {
        this.onGlobalUpdate = listener;
    }

    public void save() {
        if (getError().isEmpty()) {
            saveConsumer.accept(value);
        }
    }

    public Optional<Text> getError() {
        return errorSupplier.apply(value);
    }

    public void updateErrorState() {
        if (labelComponent == null) return;

        Optional<Text> error = getError();
        if (error.isPresent()) {
            labelComponent.text(label.copy().append(Text.literal(" (" + error.get().getString() + ")").formatted(net.minecraft.util.Formatting.RED)));
        } else {
            labelComponent.text(label);
        }
    }

    public abstract Component createComponent();
}