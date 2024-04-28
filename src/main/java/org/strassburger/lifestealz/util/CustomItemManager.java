package org.strassburger.lifestealz.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.LifeStealZ;

import javax.swing.*;
import java.util.*;
import java.util.logging.Logger;

public class CustomItemManager {
    public static final NamespacedKey CUSTOM_ITEM_TYPE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customitemtype");
    public static final NamespacedKey CUSTOM_HEART_VALUE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customheartvalue");

    public static Map<UUID, Long> lastHeartUse = new HashMap<>();

    private CustomItemManager() {}

    /**
     * Creates a custom item
     *
     * @param itemId The id of the item
     * @return The custom item
     */
    public static ItemStack createCustomItem(String itemId) {
        FileConfiguration config = LifeStealZ.getInstance().getConfig();

        CustomItem ci = new CustomItem(Material.valueOf(config.getString("items." + itemId + ".material")))
                .setName(config.getString("items." + itemId + ".name"))
                .setLore(config.getStringList("items." + itemId + ".lore"))
                .setCustomModelID(config.getInt("items." + itemId + ".customModelData"))
                .setEnchanted(config.getBoolean("items." + itemId + ".enchanted"))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES);

        ItemMeta itemMeta = ci.getItemStack().getItemMeta();

        String customItemType = config.getString("items." + itemId + ".customItemType") != null ? Objects.requireNonNull(config.getString("items." + itemId + ".customItemType")) : "heart";
        itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING, customItemType);

        if (customItemType.equalsIgnoreCase("heart")) itemMeta.getPersistentDataContainer().set(CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER, config.getInt("items." + itemId + ".customHeartValue"));

        ci.getItemStack().setItemMeta(itemMeta);

        return ci.getItemStack();
    }

    public static ItemStack createCustomItem(String itemId, int amount) {
        return new CustomItem(createCustomItem(itemId)).setAmount(amount).getItemStack();
    }

    public static ItemStack createHeart() {
        return createCustomItem("defaultheart");
    }

    public static ItemStack createRevive() {
        return createCustomItem("revive");
    }

    public static ItemStack createCloseItem() {
        return new CustomItem(Material.BARRIER)
                .setName("&cClose")
                .setCustomModelID(999)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .getItemStack();
    }

    public static boolean isHeartItem(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING) && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase("heart");
    }

    public static boolean isReviveItem(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING) && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase("revive");
    }

    public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        skullMeta.displayName(Component.text("§d" + offlinePlayer.getName()));

        List<Component> lines = new ArrayList<>();
        lines.add(MessageUtils.getAndFormatMsg(false, "messages.revivePlayerDesc", "&7Click to revive this player"));
        lines.add(MessageUtils.formatMsg("<dark_gray>" + offlinePlayer.getUniqueId()));

        skullMeta.lore(lines);
        skullMeta.setOwningPlayer(offlinePlayer);

        head.setItemMeta(skullMeta);
        return head;
    }
}
