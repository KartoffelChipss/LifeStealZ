package org.strassburger.lifestealz.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.Objects;

public class CustomItemManager {
    public static final NamespacedKey CUSTOM_ITEM_TYPE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customitemtype");
    public static final NamespacedKey CUSTOM_HEART_VALUE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customheartvalue");

    private CustomItemManager() {}

    public static ItemStack createCustomItem(String itemId) {
        FileConfiguration config = LifeStealZ.getInstance().getConfig();

        CustomItem ci = new CustomItem(Material.valueOf(config.getString("items." + itemId + ".material")))
                .setName(config.getString("items." + itemId + ".name"))
                .setLore(config.getStringList("items." + itemId + ".lore"))
                .setCustomModelID(config.getInt("items." + itemId + ".customModelID"))
                .setEnchanted(config.getBoolean("items." + itemId + ".enchanted"))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES);

        String customItemType = config.getString("items." + itemId + ".customItemType") != null ? Objects.requireNonNull(config.getString("items." + itemId + ".customItemType")) : "heart";
        ci.getPersistentDataContainer().set(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING, customItemType);

        if (customItemType.equalsIgnoreCase("heart")) ci.getPersistentDataContainer().set(CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER, config.getInt("customHeartValue"));

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
}
