package org.strassburger.lifestealz.util.customitems;

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
import org.strassburger.lifestealz.util.MessageUtils;

import java.util.*;

public class CustomItemManager {
    public static final NamespacedKey CUSTOM_ITEM_ID_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customitemid");
    public static final NamespacedKey CUSTOM_ITEM_TYPE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customitemtype");
    public static final NamespacedKey CUSTOM_HEART_VALUE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "customheartvalue");
    public static final NamespacedKey REVIVE_PAGE_KEY = new NamespacedKey(LifeStealZ.getInstance(), "revivepage");

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

        itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, itemId);

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
                .setName(MessageUtils.getAndFormatMsg(false, "closeBtn", "&cClose"))
                .setCustomModelID(999)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .getItemStack();
    }

    public static ItemStack createBackItem(int page) {
        CustomItem ci = new CustomItem(Material.ARROW)
                .setName(MessageUtils.getAndFormatMsg(false, "backBtn", "&cBack"))
                .setCustomModelID(998)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES);

        ItemMeta itemMeta = ci.getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(REVIVE_PAGE_KEY, PersistentDataType.INTEGER, page);
        ci.getItemStack().setItemMeta(itemMeta);

        return ci.getItemStack();
    }

    public static ItemStack createNextItem(int page) {
        CustomItem ci = new CustomItem(Material.ARROW)
                .setName(MessageUtils.getAndFormatMsg(false, "nextBtn", "&cNext"))
                .setCustomModelID(997)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES);

        ItemMeta itemMeta = ci.getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(REVIVE_PAGE_KEY, PersistentDataType.INTEGER, page);
        ci.getItemStack().setItemMeta(itemMeta);

        return ci.getItemStack();
    }

    public static boolean isHeartItem(ItemStack item) {
        return item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING) && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase("heart");
    }

    public static boolean isReviveItem(ItemStack item) {
        return item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING) && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase("revive");
    }

    public static String getCustomItemId(ItemStack item) {
        if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING)) return null;
        else return item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING);
    }

    public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || offlinePlayer.getName() == null) return new CustomItem(Material.SKELETON_SKULL).setName("&dUnknown").setLore(new ArrayList<String>(List.of("&8" + UUID.randomUUID()))).getItemStack();

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

    public static CustomItemData getCustomItemData(String itemId) {
        return new CustomItemData(itemId);
    }
}
