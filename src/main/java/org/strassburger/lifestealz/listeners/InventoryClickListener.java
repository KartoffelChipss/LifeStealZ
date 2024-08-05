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
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory openInventory = player.getOpenInventory().getTopInventory();

        if (openInventory.equals(GuiManager.RECIPE_GUI_MAP.get(event.getWhoClicked().getUniqueId()))) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER) {
                event.getWhoClicked().closeInventory();
            }
        }

        if (openInventory.equals(GuiManager.REVIVE_GUI_MAP.get(event.getWhoClicked().getUniqueId()))) {
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
                    String uuidString = getLastLineOfLore(item);
                    if (uuidString == null) return;
                    UUID playerUUID = UUID.fromString(uuidString);
                    OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(playerUUID);

                    if (targetPlayer.getName() == null) {
                        player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"));
                        return;
                    }

                    if (!hasReviveCrystal(player)) {
                        LifeStealZ.getInstance().getLogger().warning("Player " + player.getName() + " tried to revive " + targetPlayer.getName() + " without a revive crystal!");
                        return;
                    }

                    PlayerData targetPlayerData = LifeStealZ.getInstance().getPlayerDataStorage().load(playerUUID);

                    int reviveMaximum = LifeStealZ.getInstance().getConfig().getInt("maxRevives");
                    if (reviveMaximum != -1 && targetPlayerData.getHasbeenRevived() >= reviveMaximum) {
                        player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.reviveMaxReached", "&cThis player has already been revived %amount% times!", new Replaceable("%amount%", targetPlayerData.getHasbeenRevived() + "")));
                        return;
                    }

                    int minHearts = LifeStealZ.getInstance().getConfig().getInt("minHearts");
                    if (targetPlayerData.getMaxhp() > minHearts * 2) {
                        player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!"));
                        return;
                    }

                    targetPlayerData.setMaxhp(LifeStealZ.getInstance().getConfig().getInt("respawnHP") * 2);
                    targetPlayerData.setHasbeenRevived(targetPlayerData.getHasbeenRevived() + 1);
                    LifeStealZ.getInstance().getPlayerDataStorage().save(targetPlayerData);

                    player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveSuccess", "&7You successfully revived &c%player%&7!", new Replaceable("%player%", targetPlayer.getName())));

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f);

                    event.getInventory().close();

                    List<String> reviveCommands = LifeStealZ.getInstance().getConfig().getStringList("reviveuseCommands");
                    for (String command : reviveCommands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()).replace("&target&", targetPlayer.getName()));
                    }

                    removeReviveCrystal(player);
                    break;
            }
        }
    }

    private void throwPermissionError(HumanEntity player) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!");
        player.sendMessage(usageMessage);
    }

    private String getLastLineOfLore(ItemStack item) {
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        List<Component> lore = item.lore();
        if (lore == null) return null;
        Component lastLore = lore.get(lore.size() - 1);
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
