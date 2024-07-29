package ru.yandex.practicum.filmorate.util.parser;

import java.beans.PropertyEditorSupport;

public class EnumPropertyEditorSupport<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> enumClass;

    private EnumPropertyEditorSupport(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public static <T extends Enum<T>> EnumPropertyEditorSupport<T> forEnum(final Class<T> enumClass) {
        return new EnumPropertyEditorSupport<>(enumClass);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Enum.valueOf(enumClass, text.toUpperCase()));
    }
}
