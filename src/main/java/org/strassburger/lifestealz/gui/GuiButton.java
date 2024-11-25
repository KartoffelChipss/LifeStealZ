package org.strassburger.lifestealz.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a GUI button in a custom inventory or menu.
 * The button is built around a player skull item that can display a title and lore.
 */
public class GuiButton {
    private final ItemStack skull;
    private final String title;
    private final TextColor titleColor;
    private final List<LoreEntry> lore;

    /**
     * Constructs a {@code GuiButton} using the provided {@link Builder}.
     *
     * @param builder The builder containing the configuration for the button.
     */
    private GuiButton(Builder builder) {
        this.skull = HeadUtil.createCustomHead(builder.textureUrl);
        this.title = builder.title;
        this.titleColor = builder.titleColor;
        this.lore = builder.lore;

        setupItemMeta();
    }

    /**
     * Configures the item metadata, including the title and lore, for the skull item.
     */
    private void setupItemMeta() {
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.displayName(Component.text(title)
                .color(titleColor)
                .decorate(TextDecoration.BOLD));

        meta.lore(lore.stream()
                .map(entry -> Component.text(entry.text)
                        .color(entry.color))
                .toList());

        skull.setItemMeta(meta);
    }

    /**
     * Retrieves the item stack representing the GUI button.
     *
     * @return The {@link ItemStack} for this button.
     */
    public ItemStack getItemStack() {
        return skull;
    }

    /**
     * Represents a single entry in the lore of the GUI button.
     */
    private static class LoreEntry {
        final String text;
        final TextColor color;

        /**
         * Constructs a lore entry with the specified text and color.
         *
         * @param text  The text for the lore entry.
         * @param color The color of the text.
         */
        LoreEntry(String text, TextColor color) {
            this.text = text;
            this.color = color;
        }
    }

    /**
     * Builder class for constructing {@link GuiButton} instances.
     */
    public static class Builder {
        private String textureUrl;
        private String title;
        private TextColor titleColor;
        private List<LoreEntry> lore = new ArrayList<>();

        /**
         * Sets the texture URL for the button's skull.
         *
         * @param textureUrl The URL of the skull texture.
         * @return This builder instance.
         */
        public Builder withTexture(String textureUrl) {
            this.textureUrl = textureUrl;
            return this;
        }

        /**
         * Sets the title for the button.
         *
         * @param title The title text to display.
         * @return This builder instance.
         */
        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the color of the button's title.
         *
         * @param color The {@link TextColor} for the title.
         * @return This builder instance.
         */
        public Builder withTitleColor(TextColor color) {
            this.titleColor = color;
            return this;
        }

        /**
         * Adds a lore entry to the button.
         *
         * @param description The description text for the lore entry.
         * @return This builder instance.
         */
        public Builder withLore(String description) {
            this.lore.add(new LoreEntry(description, Constants.Colors.DESCRIPTION));
            return this;
        }

        /**
         * Adds hover text to the button's lore.
         *
         * @param hoverText The hover text for the lore entry.
         * @return This builder instance.
         */
        public Builder withHoverText(String hoverText) {
            this.lore.add(new LoreEntry(hoverText, Constants.Colors.HOVER));
            return this;
        }

        /**
         * Builds and returns a {@link GuiButton} instance using the provided configuration.
         *
         * @return A new {@code GuiButton}.
         */
        public GuiButton build() {
            return new GuiButton(this);
        }
    }
}
