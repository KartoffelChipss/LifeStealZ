package org.strassburger.lifestealz.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.OfflinePlayer;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class GUIManager extends AbstractGUIManager {
    private static final class Layout {
        static final int ROWS = 3;
        static final int CONTENT_X = 1;
        static final int CONTENT_Y = 1;
        static final int CONTENT_WIDTH = 7;
        static final int SUPPORT_X = 2;
        static final int SUPPORT_WIDTH = 5;

        static final class Graveyard {
            static final int ROWS = 6;
            static final int ITEMS_PER_PAGE = (ROWS - 2) * COLUMNS;
        }
    }

    private final LifeStealZ plugin;

    public GUIManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    public void openGui(Player player) {
        ChestGui gui = createBaseGui("Main Menu", Layout.ROWS);
        StaticPane borderPane = createBorderPane(Layout.ROWS);
        StaticPane contentPane = new StaticPane(Layout.CONTENT_X, Layout.CONTENT_Y, Layout.CONTENT_WIDTH, 1);

        contentPane.addItem(createGraveyardButton(player), 3, 0);
        borderPane.addItem(createSupportButton(player), COLUMNS - 1, Layout.ROWS - 1);

        gui.addPane(borderPane);
        gui.addPane(contentPane);
        gui.show(player);
    }

    public void openSupportGui(Player player) {
        ChestGui gui = createBaseGui("Support", Layout.ROWS);
        StaticPane borderPane = createBorderPane(Layout.ROWS);
        StaticPane contentPane = new StaticPane(Layout.SUPPORT_X, Layout.CONTENT_Y, Layout.SUPPORT_WIDTH, 1);

        addSupportButtons(contentPane);
        borderPane.addItem(createDeveloperButton(), 0, Layout.ROWS - 1);
        borderPane.addItem(createNavigationItem(() -> openGui(player)), COLUMNS - 1, Layout.ROWS - 1);

        gui.addPane(borderPane);
        gui.addPane(contentPane);
        gui.show(player);
    }

    public void openGraveyardGui(Player player, int page) {
        ChestGui gui = createBaseGui("Graveyard", Layout.Graveyard.ROWS);
        StaticPane borderPane = createBorderPane(Layout.Graveyard.ROWS);
        StaticPane contentPane = new StaticPane(0, 1, COLUMNS, 4);

        List<UUID> eliminatedPlayers = plugin.getStorage().getEliminatedPlayers();
        int totalPages = (int) Math.ceil(eliminatedPlayers.size() / (double) Layout.Graveyard.ITEMS_PER_PAGE);

        borderPane.addItem(createReviveAllButton(player), 4, 0);
        addGraveyardPagination(borderPane, player, page, totalPages);
        borderPane.addItem(createNavigationItem(() -> openGui(player)), COLUMNS - 5, Layout.Graveyard.ROWS - 1);
        populateGraveyardContent(contentPane, eliminatedPlayers, page, player);

        gui.addPane(borderPane);
        gui.addPane(contentPane);
        gui.show(player);
    }

    private void openReviveAllConfirmationGui(Player player) {
        ChestGui gui = createBaseGui("Confirm Revive All", Layout.ROWS);
        StaticPane pane = new StaticPane(0, 0, COLUMNS, 3);

        GuiButton warningButton = new GuiButton.Builder()
                    .withTexture(Constants.Textures.GHOST)
                .withTitle("⚠ WARNING ⚠")
                .withTitleColor(Constants.Colors.TITLE)
                .withLore("This action will revive ALL players!")
                .withHoverText("This action cannot be undone!")
                .build();
        pane.addItem(new GuiItem(warningButton.getItemStack()), 4, 0);

        GuiButton confirmButton = new GuiButton.Builder()
                .withTexture(Constants.Textures.ACCEPT)
                .withTitle("✓ Confirm Revive All")
                .withTitleColor(TextColor.color(0x00FF00))
                .withLore("Click to revive everyone")
                .withHoverText("This cannot be undone!")
                .build();
        pane.addItem(new GuiItem(confirmButton.getItemStack(), event -> {
            event.setCancelled(true);
            reviveAllPlayers(player);
            event.getInventory().close();
        }), 3, 2);

        GuiButton cancelButton = new GuiButton.Builder()
                .withTexture(Constants.Textures.DENY)
                .withTitle("✗ Cancel")
                .withTitleColor(Constants.Colors.BACK)
                .withLore("Return to graveyard")
                .build();
        pane.addItem(new GuiItem(cancelButton.getItemStack(), event -> {
            event.setCancelled(true);
            openGraveyardGui(player, 0);
        }), 5, 2);

        gui.addPane(pane);
        gui.show(player);
    }

    private GuiItem createReviveAllButton(Player player) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.GHOST)
                .withTitle("✧ Revive All Players ✧")
                .withTitleColor(Constants.Colors.TITLE)
                .withLore("Revive all eliminated players")
                .withHoverText("Click to revive everyone! (Cannot be undone)")
                .build();

        return new GuiItem(button.getItemStack(), event -> {
            event.setCancelled(true);
            openReviveAllConfirmationGui(player);
        });
    }

    private void reviveAllPlayers(Player reviver) {
        List<UUID> eliminatedPlayers = plugin.getStorage().getEliminatedPlayers();

        for (UUID targetUUID : eliminatedPlayers) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            PlayerData targetData = plugin.getStorage().load(targetUUID);

            targetData.setMaxHealth(plugin.getConfig().getInt("reviveHearts") * 2);
            targetData.setHasbeenRevived(targetData.getHasbeenRevived() + 1);
            plugin.getStorage().save(targetData);

            for (String command : plugin.getConfig().getStringList("reviveuseCommands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("&player&", reviver.getName())
                                .replace("&target&", target.getName()));
            }
        }

        reviver.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveAllSuccess",
                "&7You successfully revived &call eliminated players&7!"));
        reviver.playSound(reviver.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);
    }

    private void addSupportButtons(StaticPane pane) {
        SupportButton[] buttons = {
                new SupportButton("Discord Community", Constants.Textures.DISCORD, Constants.Colors.DISCORD,
                        "Join our vibrant community!", "Click to get instant access", Constants.Urls.DISCORD),
                new SupportButton("Knowledge Base", Constants.Textures.WIKI, Constants.Colors.WIKI,
                        "Explore our detailed guides", "Click to access the Wiki", Constants.Urls.WIKI),
                new SupportButton("Leave a Review", Constants.Textures.REVIEW, Constants.Colors.REVIEW,
                        "Share your experience!", "Click to rate the plugin", Constants.Urls.REVIEW)
        };

        for (int i = 0; i < buttons.length; i++) {
            pane.addItem(createSupportButtonItem(buttons[i]), i + 1, 0);
        }
    }

    private record SupportButton(String title, String texture, TextColor color,
                                 String description, String hoverText, String url) {}

    private GuiItem createSupportButtonItem(SupportButton button) {
        GuiButton guiButton = new GuiButton.Builder()
                .withTexture(button.texture())
                .withTitle("✧ " + button.title() + " ✧")
                .withTitleColor(button.color())
                .withLore(button.description())
                .withHoverText(button.hoverText())
                .build();

        return new GuiItem(guiButton.getItemStack(), event -> {
            event.setCancelled(true);
            sendClickableLink(event.getWhoClicked(), button);
        });
    }

    private void populateGraveyardContent(StaticPane pane, List<UUID> players, int page, Player viewer) {
        int startIndex = page * Layout.Graveyard.ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + Layout.Graveyard.ITEMS_PER_PAGE, players.size());

        for (int i = startIndex; i < endIndex; i++) {
            int slot = i - startIndex;
            pane.addItem(createPlayerHeadItem(viewer, players.get(i)),
                    slot % COLUMNS, slot / COLUMNS);
        }
    }

    private void addGraveyardPagination(StaticPane pane, Player player, int currentPage, int totalPages) {
        boolean hasNextPage = currentPage < totalPages - 1;
        boolean hasPrevPage = currentPage > 0;

        pane.addItem(createPaginationButton("Previous Page", Constants.Icons.ARROW_LEFT,
                        hasPrevPage ? Constants.Textures.ARROW_LEFT : Constants.Textures.ARROW_LEFT_DISABLED,
                        hasPrevPage, () -> openGraveyardGui(player, currentPage - 1)),
                0, Layout.Graveyard.ROWS - 1);

        pane.addItem(createPaginationButton("Next Page", Constants.Icons.ARROW_RIGHT,
                        hasNextPage ? Constants.Textures.ARROW_RIGHT : Constants.Textures.ARROW_RIGHT_DISABLED,
                        hasNextPage, () -> openGraveyardGui(player, currentPage + 1)),
                8, Layout.Graveyard.ROWS - 1);
    }

    private GuiItem createPaginationButton(String title, String arrow, String texture,
                                           boolean enabled, Runnable action) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(texture)
                .withTitle(arrow + " " + title)
                .withTitleColor(enabled ? Constants.Colors.TITLE : Constants.Colors.DISABLED)
                .build();

        return new GuiItem(button.getItemStack(), event -> {
            event.setCancelled(true);
            if (enabled) action.run();
        });
    }

    private GuiItem createPlayerHeadItem(Player player, UUID targetUUID) {
        ItemStack head = HeadUtil.getAmbiguousPlayerHead(targetUUID);

        return new GuiItem(head, event -> {
            event.setCancelled(true);
            revivePlayer(player, targetUUID);
            event.getInventory().close();
        });
    }

    private void revivePlayer(Player reviver, UUID targetUUID) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
        PlayerData targetData = plugin.getStorage().load(targetUUID);

        targetData.setMaxHealth(plugin.getConfig().getInt("reviveHearts") * 2);
        targetData.setHasbeenRevived(targetData.getHasbeenRevived() + 1);
        plugin.getStorage().save(targetData);

        reviver.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveSuccess",
                "&7You successfully revived &c%player%&7!",
                new MessageUtils.Replaceable("%player%", target.getName())));
        reviver.playSound(reviver.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);

        for (String command : plugin.getConfig().getStringList("reviveuseCommands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    command.replace("&player&", reviver.getName())
                            .replace("&target&", target.getName()));
        }
    }

    private GuiItem createGraveyardButton(Player player) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.GRAVEYARD)
                .withTitle("✧ Graveyard ✧")
                .withTitleColor(Constants.Colors.TITLE)
                .withLore("Revive eliminated players")
                .withHoverText("Click to view eliminated players!")
                .build();

        return new GuiItem(button.getItemStack(), event -> {
            event.setCancelled(true);
            openGraveyardGui(player, 0);
        });
    }

    private GuiItem createSupportButton(Player player) {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.SUPPORT)
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

    private GuiItem createDeveloperButton() {
        GuiButton button = new GuiButton.Builder()
                .withTexture(Constants.Textures.DEVELOPER)
                .withTitle("❤ Built with Love ❤")
                .withTitleColor(Constants.Colors.LOVE)
                .withHoverText("by KartoffelChipss")
                .build();

        return new GuiItem(button.getItemStack(), event -> event.setCancelled(true));
    }

    private void sendClickableLink(org.bukkit.entity.HumanEntity player, SupportButton button) {
        Component message = Component.text()
                .content("✦ ")
                .color(button.color())
                .append(Component.text("Click here to "))
                .color(Constants.Colors.DESCRIPTION)
                .append(Component.text(button.title())
                        .color(button.color())
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(button.url()))
                        .hoverEvent(HoverEvent.showText(
                                Component.text(button.hoverText())
                                        .color(Constants.Colors.HOVER))))
                .append(Component.text(" ✦"))
                .color(button.color())
                .build();

        player.sendMessage(message);
        openUrlInBrowser(button.url());
    }

    private void openUrlInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to open URL: " + url, e);
        }
    }
}