package org.strassburger.lifestealz.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public abstract class AbstractGUIManager {
    protected static final int COLUMNS = 9;

    /**
     * Creates a base GUI with the specified title and number of rows
     *
     * @param title The title of the GUI
     * @param rows The number of rows in the GUI
     * @return A configured ChestGui
     */
    protected ChestGui createBaseGui(String title, int rows) {
        Component titleComponent = Component.text(Constants.Icons.GUI_ICON.getValue())
                .color(Constants.Colors.GREY.getValue())
                .append(Component.text(Constants.Icons.SEPARATOR.getValue()))
                .append(Component.text(title)
                        .color(Constants.Colors.TITLE.getValue())
                        .decorate(TextDecoration.BOLD));

        ChestGui gui = new ChestGui(rows, title);
        gui.setTitle(ComponentHolder.of(titleComponent));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        return gui;
    }

    /**
     * Creates a border pane for the GUI
     *
     * @param rows The number of rows in the GUI
     * @return A StaticPane with border items
     */
    protected StaticPane createBorderPane(int rows) {
        StaticPane borderPane = new StaticPane(0, 0, COLUMNS, rows);
        GuiItem borderItem = createBorderItem();

        for (int col = 0; col < COLUMNS; col++) {
            borderPane.addItem(borderItem, col, 0);
            borderPane.addItem(borderItem, col, rows - 1);
        }
        return borderPane;
    }

    /**
     * Creates a border item for the GUI
     *
     * @return A GuiItem representing the border
     */
    protected GuiItem createBorderItem() {
        return createBorderItem(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Creates a border item with a specific material
     *
     * @param material The material to use for the border
     * @return A GuiItem representing the border
     */
    protected GuiItem createBorderItem(org.bukkit.Material material) {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.empty());
        item.setItemMeta(meta);
        return new GuiItem(item, event -> event.setCancelled(true));
    }

    /**
     * Creates a navigation item with a custom callback
     *
     * @param callback The navigation callback to execute
     * @return A GuiItem for navigation
     */
    protected GuiItem createNavigationItem(GuiNavigationCallback callback) {
        return new GuiItem(
                new GuiButton.Builder()
                        .withTexture(Constants.Textures.HOME.getValue())
                        .withTitle("« Return »")
                        .withTitleColor(Constants.Colors.TITLE.getValue())
                        .withLore("Go back to main menu")
                        .build().getItemStack(),
                event -> {
                    event.setCancelled(true);
                    callback.navigate();
                }
        );
    }

    /**
     * Functional interface for GUI navigation callbacks
     */
    @FunctionalInterface
    public interface GuiNavigationCallback {
        void navigate();
    }
}