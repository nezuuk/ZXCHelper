package ru.emrass.zxchelper.ui.lib;

import net.minecraft.text.Text;
import ru.emrass.zxchelper.ui.lib.elements.*;

import java.util.List;

public class ZxcEntryBuilder {

    public static ZxcEntryBuilder create() {
        return new ZxcEntryBuilder();
    }

    public InputStringElement startStringField(Text name, String value) {
        return new InputStringElement(name, value);
    }

    public ToggleElement startBooleanToggle(Text name, boolean value) {
        return new ToggleElement(name, value, null);
    }

    public DropdownElement startDropdownMenu(Text name, String value, List<String> options) {
        return new DropdownElement(name, value, options);
    }

    public StringListElement startStrList(Text name, List<String> value) {
        return new StringListElement(name, value);
    }

    public ColorElement startColorPicker(Text name, int value) {
        return new ColorElement(name, value, null);
    }

}