package org.strassburger.lifestealz.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand(); // Track which hand is being used

        boolean worldIsWhitelisted = LifeStealZ.getInstance().getConfig().getStringList("worlds").contains(player.getLocation().getWorld().getName());

        if (event.getAction().isRightClick() && item != null) {
            if (!worldIsWhitelisted && (CustomItemManager.isHeartItem(item) || CustomItemManager.isReviveItem(item))) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
                return;
            }

            if (CustomItemManager.isHeartItem(item)) {
                long heartCooldown = LifeStealZ.getInstance().getConfig().getLong("heartCooldown");
                if (CooldownManager.lastHeartUse.get(player.getUniqueId()) != null && CooldownManager.lastHeartUse.get(player.getUniqueId()) + heartCooldown > System.currentTimeMillis()) {
                    player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.heartconsumeCooldown", "&cYou have to wait before using another heart!"));
                    return;
                }

                PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

                Integer savedHeartAmountInteger = item.getItemMeta().getPersistentDataContainer().has(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) ? item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) : 1;
                int savedHeartAmount = savedHeartAmountInteger != null ? savedHeartAmountInteger : 1;
                double heartsToAdd = savedHeartAmount * 2;
                double newHearts = playerData.getMaxhp() + heartsToAdd;

                double maxHearts = LifeStealZ.getInstance().getConfig().getInt("maxHearts") * 2;

                if (newHearts > maxHearts) {
                    player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new Replaceable("%limit%", Integer.toString((int) maxHearts / 2))));
                    return;
                }

                if (hand == EquipmentSlot.HAND) {
                    updateItemInHand(player, item, player.getInventory().getHeldItemSlot());
                } else if (hand == EquipmentSlot.OFF_HAND) {
                    updateItemInHand(player, item, 40);
                }

                playerData.setMaxhp(newHearts);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, newHearts);
                player.setHealth(Math.min(player.getHealth() + heartsToAdd, newHearts));

                String customItemID = CustomItemManager.getCustomItemId(item);
                System.out.printf("customItemID: %s\n", customItemID);
                if (customItemID != null) {
                    CustomItemData.CustomItemSoundData sound = CustomItemManager.getCustomItemData(customItemID).getSound();
                    if (sound.isEnabled()) player.playSound(player.getLocation(), sound.getSound(), (float) sound.getVolume(), (float) sound.getPitch());
                }

                List<String> heartuseCommands = LifeStealZ.getInstance().getConfig().getStringList("heartuseCommands");
                for (String command : heartuseCommands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()));
                }

                if (LifeStealZ.getInstance().getConfig().getBoolean("playTotemEffect")) player.playEffect(EntityEffect.TOTEM_RESURRECT);

                player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.heartconsume", "&7Consumed a heart and got &c%amount% &7hearts!", new Replaceable("%amount%", savedHeartAmount + "")));
                CooldownManager.lastHeartUse.put(player.getUniqueId(), System.currentTimeMillis());
            }

            if (CustomItemManager.isReviveItem(item)) {
                GuiManager.openReviveGui(player, 1);
            }
        }
    }

    private void updateItemInHand(Player player, ItemStack item, int slot) {
        ItemStack updatedItem = item.clone();
        updatedItem.setAmount(item.getAmount() - 1);

        if (updatedItem.getAmount() > 0) updatedItem.setItemMeta(item.getItemMeta());

        player.getInventory().setItem(slot, updatedItem);
    }
}