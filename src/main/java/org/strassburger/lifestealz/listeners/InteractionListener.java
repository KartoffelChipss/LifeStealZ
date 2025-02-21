package org.strassburger.lifestealz.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.*;
import org.strassburger.lifestealz.util.customitems.CustomItemData;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class InteractionListener implements Listener {
    private final LifeStealZ plugin;

    public InteractionListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand(); // Track which hand is being used

        // Prevent using respawn anchors in the overworld
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && plugin.getConfig().getBoolean("preventRespawnAnchors")) {
            Block block = event.getClickedBlock();
            if (
                    block != null
                    && block.getType() == Material.RESPAWN_ANCHOR
                    && player.getWorld().getEnvironment() == World.Environment.NORMAL
            ) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction().isRightClick() && item != null) {
            if (CustomItemManager.isForbiddenItem(item)) {
                event.setCancelled(true);

                if (hand == null) return;

                switch (hand) {
                    case HAND:
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
                        break;
                    case OFF_HAND:
                        player.getInventory().setItem(40, new ItemStack(Material.AIR));
                        break;
                }
            }

            if (
                    !WhitelistManager.isWorldWhitelisted(player)
                    && (CustomItemManager.isHeartItem(item) || CustomItemManager.isReviveItem(item))
            ) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
                return;
            }

            if (CustomItemManager.isHeartItem(item)) {
                handleHeartItem(item, player, hand, event);
            }

            if (CustomItemManager.isReviveItem(item)) {
                handleReviveItem(item, player, hand, event);
            }
        }
    }

    private void handleHeartItem(ItemStack item, Player player, EquipmentSlot hand, PlayerInteractEvent event) {
        CustomItemData customItemData = CustomItemManager.getCustomItemData(CustomItemManager.getCustomItemId(item));

        if (customItemData == null) return;

        event.setCancelled(true);

        if (customItemData.requiresPermission() && !player.hasPermission(customItemData.getPermission()) && !player.isOp() && !player.hasPermission("lifestealz.item.*")) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!"));
            return;
        }

        if (restrictedHeartByGracePeriod(player)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "noHeartUseInGracePeriod",
                    "&cYou can't use hearts during the grace period!"
            ));
            return;
        }

        long heartCooldown = plugin.getConfig().getLong("heartCooldown");
        if (CooldownManager.lastHeartUse.get(player.getUniqueId()) != null && CooldownManager.lastHeartUse.get(player.getUniqueId()) + heartCooldown > System.currentTimeMillis()) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "heartconsumeCooldown", "&cYou have to wait before using another heart!"));
            return;
        }

        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

        Integer savedHeartAmountInteger = item.getItemMeta().getPersistentDataContainer().has(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) ? item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) : 1;
        int savedHeartAmount = savedHeartAmountInteger != null ? savedHeartAmountInteger : 1;
        double heartsToAdd = savedHeartAmount * 2;
        double newHearts = playerData.getMaxHealth() + heartsToAdd;

        double maxHearts = plugin.getConfig().getInt("maxHearts") * 2;

        if (newHearts > maxHearts) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new MessageUtils.Replaceable("%limit%", Integer.toString((int) maxHearts / 2))));
            return;
        }

        if (playerData.getMaxHealth() < customItemData.getMinHearts() * 2 && customItemData.getMinHearts() != -1) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "itemMinHearts", "&cYou need at least %amount% hearts to use this item!", new MessageUtils.Replaceable("%amount%", Integer.toString(customItemData.getMinHearts()))));
            return;
        }

        if (playerData.getMaxHealth() >= customItemData.getMaxHearts() * 2 && customItemData.getMaxHearts() != -1) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "itemMaxHearts", "&cYou can't use this item with more than %amount% hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString(customItemData.getMaxHearts()))));
            return;
        }

        if (hand == EquipmentSlot.HAND) {
            updateItemInHand(player, item, player.getInventory().getHeldItemSlot());
        } else if (hand == EquipmentSlot.OFF_HAND) {
            updateItemInHand(player, item, 40);
        }

        playerData.setMaxHealth(newHearts);
        plugin.getStorage().save(playerData);
        LifeStealZ.setMaxHealth(player, newHearts);
        if (plugin.getConfig().getBoolean("healOnHeartUse")) player.setHealth(Math.min(player.getHealth() + heartsToAdd, newHearts));

        String customItemID = CustomItemManager.getCustomItemId(item);
        if (customItemID != null) {
            CustomItemData.CustomItemSoundData sound = CustomItemManager.getCustomItemData(customItemID).getSound();
            if (sound.isEnabled()) player.playSound(player.getLocation(), sound.getSound(), (float) sound.getVolume(), (float) sound.getPitch());
        }

        List<String> heartuseCommands = plugin.getConfig().getStringList("heartuseCommands");
        for (String command : heartuseCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()));
        }

        if (plugin.getConfig().getBoolean("playTotemEffect")) player.playEffect(EntityEffect.TOTEM_RESURRECT);

        player.sendMessage(MessageUtils.getAndFormatMsg(true, "heartconsume", "&7Consumed a heart and got &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", savedHeartAmount + "")));
        CooldownManager.lastHeartUse.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void handleReviveItem(ItemStack item, Player player, EquipmentSlot hand, PlayerInteractEvent event) {
        event.setCancelled(true);
        GuiManager.openReviveGui(player, 1);
    }

    private void updateItemInHand(Player player, ItemStack item, int slot) {
        ItemStack updatedItem = item.clone();
        updatedItem.setAmount(item.getAmount() - 1);

        if (updatedItem.getAmount() > 0) updatedItem.setItemMeta(item.getItemMeta());

        player.getInventory().setItem(slot, updatedItem);
    }

    private boolean restrictedHeartByGracePeriod(Player player) {
        GracePeriodManager gracePeriodManager = plugin.getGracePeriodManager();
        return gracePeriodManager.isInGracePeriod(player) && !gracePeriodManager.getConfig().useHearts();
    }
}