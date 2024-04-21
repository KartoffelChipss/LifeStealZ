package org.strassburger.lifestealz.util;

public class Replaceable {
    private final String placeholder;
    private final String value;

    public Replaceable(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getValue() {
        return value;
    }
}
