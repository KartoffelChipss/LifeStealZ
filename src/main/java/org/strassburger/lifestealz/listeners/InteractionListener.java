package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.*;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;
import java.util.UUID;

public class InteractionListener implements Listener {
    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        boolean worldIsWhitelisted = LifeStealZ.getInstance().getConfig().getStringList("worlds").contains(player.getLocation().getWorld().getName());

        if (event.getAction().isRightClick() && event.getItem() != null) {
            if (!worldIsWhitelisted) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
                return;
            }

            if (CustomItemManager.isHeartItem(event.getItem())) {
                long heartCooldown = LifeStealZ.getInstance().getConfig().getLong("heartCooldown");
                if (CustomItemManager.lastHeartUse.get(player.getUniqueId()) != null && CustomItemManager.lastHeartUse.get(player.getUniqueId()) + heartCooldown > System.currentTimeMillis()) {
                    player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.heartconsumeCooldown", "&cYou have to wait before using another heart!"));
                    return;
                }

                PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

                ItemStack item = event.getItem().clone();

                int savedHeartAmount = item.getItemMeta().getPersistentDataContainer().has(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) ? item.getItemMeta().getPersistentDataContainer().get(CustomItemManager.CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER) : 1;
                double heartsToAdd = savedHeartAmount * 2;
                double newHearts = playerData.getMaxhp() + heartsToAdd;

                double maxHearts = LifeStealZ.getInstance().getConfig().getInt("maxHearts") * 2;

                if (newHearts > maxHearts) {
                    player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new Replaceable("%limit%", Integer.toString((int) maxHearts / 2))));
                    return;
                }

                event.getItem().setAmount(event.getItem().getAmount() - 1);

                playerData.setMaxhp(newHearts);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, newHearts);
                player.setHealth(Math.min(player.getHealth() + heartsToAdd, newHearts));

                if (LifeStealZ.getInstance().getConfig().getBoolean("heartuseSound.enabled")) {
                    Sound sound = Sound.valueOf(LifeStealZ.getInstance().getConfig().getString("heartuseSound.sound"));
                    float volume = (float) LifeStealZ.getInstance().getConfig().getDouble("heartuseSound.volume");
                    float pitch = (float) LifeStealZ.getInstance().getConfig().getDouble("heartuseSound.pitch");
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }

                List<String> heartuseCommands = LifeStealZ.getInstance().getConfig().getStringList("heartuseCommands");
                for (String command : heartuseCommands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("&player&", player.getName()));
                }

                if (LifeStealZ.getInstance().getConfig().getBoolean("playTotemEffect")) player.playEffect(EntityEffect.TOTEM_RESURRECT);

                player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.heartconsume", "&7Cansumed a heart and got &c%amount% &7hearts!", new Replaceable("%amount%", "1")));
                CustomItemManager.lastHeartUse.put(player.getUniqueId(), System.currentTimeMillis());
            }

            if (CustomItemManager.isReviveItem(event.getItem())) {
                List<UUID> eliminatedPlayers = LifeStealZ.getInstance().getPlayerDataStorage().getEliminatedPlayers();

                Inventory inventory = Bukkit.createInventory(null, 6 * 9, MessageUtils.getAndFormatMsg(false, "messages.reviveTitle", "&8Revive a player"));

                addNavbar(inventory);

                for (UUID eliminatedPlayerUUID : eliminatedPlayers) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(eliminatedPlayerUUID);
                    inventory.addItem(CustomItemManager.getPlayerHead(offlinePlayer));
                }

                player.openInventory(inventory);
                GuiManager.REVIVE_GUI_MAP.put(player.getUniqueId(), inventory);
            }
        }
    }

    private void addNavbar(Inventory inventory) {
        inventory.setItem(49, CustomItemManager.createCloseItem());

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.text("§r "));
        glass.setItemMeta(glassMeta);
        int[] glassSlots = {45, 46, 47, 48, 50, 51, 52, 53};
        for (int slot : glassSlots) {
            inventory.setItem(slot, glass);
        }
    }
}
