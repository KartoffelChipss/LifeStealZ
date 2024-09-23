package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.*;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;
import java.util.UUID;

public class InventoryClickListener implements Listener {
    private final LifeStealZ plugin;

    public InventoryClickListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory openInventory = player.getOpenInventory().getTopInventory();

        if (openInventory.equals(GuiManager.RECIPE_GUI_MAP.get(event.getWhoClicked().getUniqueId()))) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                event.getWhoClicked().closeInventory();
            }

            return;
        }

        if (openInventory.equals(GuiManager.REVIVE_GUI_MAP.get(event.getWhoClicked().getUniqueId()))) {
            int reviveMaximum = plugin.getConfig().getInt("maxRevives");
            int minHearts = plugin.getConfig().getInt("minHearts");

            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR || !(event.getWhoClicked() instanceof Player)) return;

            switch (item.getType()) {
                case BARRIER:
                    player.closeInventory();
                    break;
                case ARROW:
                    event.setCancelled(true);
                    Integer pageInt = item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.REVIVE_PAGE_KEY, PersistentDataType.INTEGER);
                    int page = pageInt != null ? pageInt : 1;
                    GuiManager.openReviveGui(player, page);
                case PLAYER_HEAD:
                    if (!player.hasPermission("lifestealz.revive")) {
                        throwPermissionError(player);
                        return;
                    }

                    if (item.lore() == null) return;
                    String uuidString = getLastLineOfLore(item, false);
                    if (uuidString == null) return;
                    UUID playerUUID = UUID.fromString(uuidString);
                    OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(playerUUID);

                    if (targetPlayer.getName() == null) {
                        player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
                        return;
                    }

                    if (!hasReviveCrystal(player)) {
                        plugin.getLogger().warning("Player " + player.getName() + " tried to revive " + targetPlayer.getName() + " without a revive crystal!");
                        return;
                    }

                    PlayerData targetPlayerData = plugin.getStorage().load(playerUUID);

                    if (reviveMaximum != -1 && targetPlayerData.getHasbeenRevived() >= reviveMaximum) {
                        player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.reviveMaxReached", "&cThis player has already been revived %amount% times!", new MessageUtils.Replaceable("%amount%", targetPlayerData.getHasbeenRevived() + "")));
                        return;
                    }

                    if (targetPlayerData.getMaxHealth() > minHearts * 2) {
                        player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!"));
                        return;
                    }

                    targetPlayerData.setMaxHealth(plugin.getConfig().getInt("reviveHearts") * 2);
                    targetPlayerData.setHasbeenRevived(targetPlayerData.getHasbeenRevived() + 1);
                    plugin.getStorage().save(targetPlayerData);

                    player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveSuccess", "&7You successfully revived &c%player%&7!", new MessageUtils.Replaceable("%player%", targetPlayer.getName())));

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);

                    event.getInventory().close();

                    List<String> reviveCommands = plugin.getConfig().getStringList("reviveuseCommands");
                    for (String command : reviveCommands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()).replace("&target&", targetPlayer.getName()));
                    }

                    removeReviveCrystal(player);

                    plugin.getWebHookManager().sendWebhookMessage(WebHookManager.WebHookType.REVIVE, targetPlayer.getName(), player.getName());
                    break;
                case SKELETON_SKULL:
                    if (!player.hasPermission("lifestealz.revive")) {
                        throwPermissionError(player);
                        return;
                    }

                    if (item.lore() == null) return;
                    String bedrockUuidString = getLastLineOfLore(item, true);
                    if (bedrockUuidString == null) return;
                    UUID bedrockPlayerUUID = UUID.fromString(bedrockUuidString);
                    OfflinePlayer targetBedrockPlayer = Bukkit.getServer().getOfflinePlayer(bedrockPlayerUUID);

                    if (targetBedrockPlayer.getName() == null) {
                        player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
                        return;
                    }

                    if (!hasReviveCrystal(player)) {
                        plugin.getLogger().warning("Player " + player.getName() + " tried to revive " + targetBedrockPlayer.getName() + " without a revive crystal!");
                        return;
                    }

                    PlayerData targetBedrockPlayerData = plugin.getStorage().load(bedrockPlayerUUID);


                    if (reviveMaximum != -1 && targetBedrockPlayerData.getHasbeenRevived() >= reviveMaximum) {
                        player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.reviveMaxReached", "&cThis player has already been revived %amount% times!", new MessageUtils.Replaceable("%amount%", targetBedrockPlayerData.getHasbeenRevived() + "")));
                        return;
                    }


                    if (targetBedrockPlayerData.getMaxHealth() > minHearts * 2) {
                        player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!"));
                        return;
                    }

                    targetBedrockPlayerData.setMaxHealth(plugin.getConfig().getInt("reviveHearts") * 2);
                    targetBedrockPlayerData.setHasbeenRevived(targetBedrockPlayerData.getHasbeenRevived() + 1);
                    plugin.getStorage().save(targetBedrockPlayerData);

                    player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveSuccess", "&7You successfully revived &c%player%&7!", new MessageUtils.Replaceable("%player%", targetBedrockPlayer.getName())));

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);

                    event.getInventory().close();

                    List<String> bedrockReviveCommands = plugin.getConfig().getStringList("reviveuseCommands");
                    for (String command : bedrockReviveCommands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()).replace("&target&", targetBedrockPlayer.getName()));
                    }

                    removeReviveCrystal(player);
                    break;
            }

            return;
        }

        if (event.getCurrentItem() != null && CustomItemManager.isForbiddenItem(event.getCurrentItem())) {
            event.setCancelled(true);
            event.setCurrentItem(new ItemStack(Material.AIR));
        }
    }

    private void throwPermissionError(HumanEntity player) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(usageMessage);
    }

    private String getLastLineOfLore(ItemStack item, boolean bedrock) {
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        List<Component> lore = item.lore();
        if (lore == null) return null;
        int line;
        if(bedrock) line = 2; else line = 1;
        Component lastLore = lore.get(lore.size() - line);
        return plainSerializer.serialize(lastLore);
    }

    private boolean hasReviveCrystal(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && CustomItemManager.isReviveItem(item)) return true;
        }
        return false;
    }

    private void removeReviveCrystal(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && CustomItemManager.isReviveItem(item)) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }
}
