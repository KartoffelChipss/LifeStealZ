package org.strassburger.lifestealz.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.strassburger.lifestealz.LifeStealZ;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Level;

public class GUIManager {
    private static final int GUI_ROWS = 3;
    private static final int GUI_COLUMNS = 9;
    private static final int CONTENT_START_X = 1;
    private static final int CONTENT_START_Y = 1;
    private static final int CONTENT_WIDTH = 7;
    private static final int SUPPORT_CONTENT_START_X = 2;
    private static final int SUPPORT_CONTENT_WIDTH = 5;

    private final LifeStealZ plugin;

    public GUIManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    // Main Menu GUI Methods
    public void openGui(Player player) {
        ChestGui gui = createBaseGui("Main Menu");
        StaticPane borderPane = createBorderPane();
        StaticPane contentPane = new StaticPane(CONTENT_START_X, CONTENT_START_Y, CONTENT_WIDTH, 1);

        GuiItem supportButton = createSupportButton(player);
        borderPane.addItem(supportButton, GUI_COLUMNS - 1, GUI_ROWS - 1);

        gui.addPane(borderPane);
        gui.addPane(contentPane);
        gui.show(player);
    }

    // Support GUI Methods
    public void openSupportGui(Player player) {
        ChestGui supportGui = createBaseGui("Support");
        StaticPane borderPane = createBorderPane();
        StaticPane contentPane = new StaticPane(SUPPORT_CONTENT_START_X, CONTENT_START_Y, SUPPORT_CONTENT_WIDTH, 1);

        addSupportButtons(contentPane, player);
        addNavigationButtons(borderPane, player);

        supportGui.addPane(borderPane);
        supportGui.addPane(contentPane);
        supportGui.show(player);
    }

    // GUI Creation Helper Methods
    private ChestGui createBaseGui(String title) {
        Component titleComponent = createGuiTitle(title);
        ChestGui gui = new ChestGui(GUI_ROWS, ComponentHolder.of(titleComponent));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        return gui;
    }

    private Component createGuiTitle(String title) {
        return Component.text(Constants.Icons.GUI_ICON)
                .color(Constants.Colors.GREY)
                .append(Component.text(Constants.Icons.SEPARATOR))
                .color(Constants.Colors.GREY)
                .append(Component.text(title)
                        .color(Constants.Colors.TITLE)
                        .decorate(TextDecoration.BOLD));
    }

    private StaticPane createBorderPane() {
        StaticPane borderPane = new StaticPane(0, 0, GUI_COLUMNS, GUI_ROWS);
        GuiItem borderItem = createBorderItem();

        for (int row = 0; row < GUI_ROWS; row++) {
            if (row == 0 || row == GUI_ROWS - 1) {
                for (int col = 0; col < GUI_COLUMNS; col++) {
                    borderPane.addItem(borderItem, col, row);
                }
            }
        }
        return borderPane;
    }

    private GuiItem createBorderItem() {
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = borderItem.getItemMeta();
        borderMeta.displayName(Component.empty());
        borderItem.setItemMeta(borderMeta);
        return new GuiItem(borderItem, event -> event.setCancelled(true));
    }

    // Button Creation Methods
    private void addSupportButtons(StaticPane contentPane, Player player) {
        GuiItem discordButton = createLinkButton(
                "Discord Community",
                Constants.Textures.DISCORD_HEAD,
                Constants.Colors.DISCORD,
                "Join our vibrant community!",
                "Click to get instant access",
                Constants.Urls.DISCORD,
                player
        );

        GuiItem wikiButton = createLinkButton(
                "Knowledge Base",
                Constants.Textures.WIKI_HEAD,
                Constants.Colors.WIKI,
                "Explore our detailed guides",
                "Click to access the Wiki",
                Constants.Urls.WIKI,
                player
        );

        GuiItem reviewButton = createLinkButton(
                "Leave a Review",
                Constants.Textures.REVIEW_HEAD,
                Constants.Colors.REVIEW,
                "Share your experience!",
                "Click to rate the plugin",
                Constants.Urls.REVIEW,
                player
        );

        contentPane.addItem(discordButton, 1, 0);
        contentPane.addItem(wikiButton, 2, 0);
        contentPane.addItem(reviewButton, 3, 0);
    }

    private void addNavigationButtons(StaticPane borderPane, Player player) {
        GuiItem backButton = createBackButton(player);
        GuiItem developerButton = createDeveloperButton();

        borderPane.addItem(developerButton, 0, GUI_ROWS - 1);
        borderPane.addItem(backButton, GUI_COLUMNS - 1, GUI_ROWS - 1);
    }

    private GuiItem createSupportButton(Player player) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.SUPPORT_HEAD)
                .withTitle("✧ Support Hub ✧")
                .withTitleColor(Constants.Colors.SUPPORT)
                .withLore("Need assistance with the plugin?")
                .withHoverText("Click to explore support options!")
                .build();

        return new GuiItem(button.getItemStack(), event -> {
            event.setCancelled(true);
            openSupportGui(player);
        });
    }

    /**
     * You can of course directly utilize the InventoryFramework API's methods instead, but i find it more helpful to utilize these helper classes.
     * This way it's easier to create a common / universal styling, and its far FAR less work replicating.
     */

    private GuiItem createLinkButton(String title, String texture, TextColor color,
                                     String description, String hoverText, String url,
                                     Player player) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(texture)
                .withTitle("✧ " + title + " ✧")
                .withTitleColor(color)
                .withLore(description)
                .withHoverText(hoverText)
                .build();

        return new GuiItem(button.getItemStack(), event -> {
            event.setCancelled(true);
            sendClickableLink(player, title, url, description, color);
        });
    }

    private GuiItem createBackButton(Player player) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.BACK_HEAD)
                .withTitle("« Return »")
                .withTitleColor(Constants.Colors.TITLE)
                .withLore("Go back to main menu")
                .build();

        return new GuiItem(button.getItemStack(), event -> {
            event.setCancelled(true);
            openGui(player);
        });
    }

    private GuiItem createDeveloperButton() {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.DEVELOPER_HEAD)
                .withTitle("❤ Built with Love ❤")
                .withTitleColor(Constants.Colors.LOVE)
                .withHoverText("by KartoffelChipss")
                .build();

        return new GuiItem(button.getItemStack(), event -> event.setCancelled(true));
    }

    // Link Handling
    private void sendClickableLink(Player player, String linkText, String url,
                                   String hoverText, TextColor color) {
        sendClickableMessage(player, linkText, url, hoverText, color);
        openUrlInBrowser(url);
    }

    private void sendClickableMessage(Player player, String linkText, String url,
                                      String hoverText, TextColor color) {
        Component message = Component.text()
                .content("✦ ")
                .color(color)
                .append(Component.text("Click here to "))
                .color(Constants.Colors.DESCRIPTION)
                .append(Component.text(linkText)
                        .color(color)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(url))
                        .hoverEvent(HoverEvent.showText(
                                Component.text(hoverText)
                                        .color(Constants.Colors.HOVER))))
                .append(Component.text(" ✦"))
                .color(color)
                .build();

        player.sendMessage(message);
    }

    private void openUrlInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to open URL: " + url, e);
        }
    }
}