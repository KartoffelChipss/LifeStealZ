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
import org.jetbrains.annotations.Nullable;
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
        FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();

        CustomItem ci = new CustomItem(Material.valueOf(config.getString(itemId + ".material")))
                .setName(config.getString(itemId + ".name"))
                .setLore(config.getStringList(itemId + ".lore"))
                .setCustomModelID(config.getInt(itemId + ".customModelData"))
                .setEnchanted(config.getBoolean(itemId + ".enchanted"))
                .addFlag(ItemFlag.HIDE_ATTRIBUTES);

        ItemMeta itemMeta = ci.getItemStack().getItemMeta();

        itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, itemId);

        String customItemType = config.getString(itemId + ".customItemType") != null ? Objects.requireNonNull(config.getString(itemId + ".customItemType")) : "heart";
        itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING, customItemType);

        if (customItemType.equalsIgnoreCase("heart")) itemMeta.getPersistentDataContainer().set(CUSTOM_HEART_VALUE_KEY, PersistentDataType.INTEGER, config.getInt( itemId + ".customHeartValue"));

        ci.getItemStack().setItemMeta(itemMeta);

        return ci.getItemStack();
    }

    /**
     * Creates a custom item with a specific amount
     *
     * @param itemId The id of the item
     * @param amount The amount of the item
     * @return The custom item
     */
    public static ItemStack createCustomItem(String itemId, int amount) {
        return new CustomItem(createCustomItem(itemId)).setAmount(amount).getItemStack();
    }

    /**
     * Creates the default heart item
     *
     * @return The default heart item
     */
    public static ItemStack createHeart() {
        return createCustomItem(LifeStealZ.getInstance().getConfig().getString("heartItem", "defaultheart"));
    }

    /**
     * Creates a close item for GUIs
     *
     * @return The close item
     */
    public static ItemStack createCloseItem() {
        return new CustomItem(Material.BARRIER)
                .setName(MessageUtils.getAndFormatMsg(false, "closeBtn", "&cClose"))
                .setCustomModelID(999)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .makeForbidden()
                .getItemStack();
    }

    /**
     * Creates a back item for paginated GUIs
     *
     * @param page The page to go back to
     * @return The back item
     */
    public static ItemStack createBackItem(int page) {
        CustomItem ci = new CustomItem(Material.ARROW)
                .setName(MessageUtils.getAndFormatMsg(false, "backBtn", "&cBack"))
                .setCustomModelID(998)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .makeForbidden();

        ItemMeta itemMeta = ci.getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(REVIVE_PAGE_KEY, PersistentDataType.INTEGER, page);
        ci.getItemStack().setItemMeta(itemMeta);

        return ci.getItemStack();
    }

    /**
     * Creates a next item for paginated GUIs
     *
     * @param page The page to go to
     * @return The next item
     */
    public static ItemStack createNextItem(int page) {
        CustomItem ci = new CustomItem(Material.ARROW)
                .setName(MessageUtils.getAndFormatMsg(false, "nextBtn", "&cNext"))
                .setCustomModelID(997)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .makeForbidden();

        ItemMeta itemMeta = ci.getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(REVIVE_PAGE_KEY, PersistentDataType.INTEGER, page);
        ci.getItemStack().setItemMeta(itemMeta);

        return ci.getItemStack();
    }

    /**
     * Checks if an item is a heart item
     *
     * @param item The item to check
     * @return If the item is a heart item
     */
    public static boolean isHeartItem(ItemStack item) {
        return item.getItemMeta() != null
                && item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING)
                && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase("heart");
    }

    /**
     * Checks if an item is a revive item
     *
     * @param item The item to check
     * @return If the item is a revive item
     */
    public static boolean isReviveItem(ItemStack item) {
        return item.getItemMeta() != null
                && item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING)
                && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase("revive");
    }

    /**
     * Checks if an item is a forbidden item
     * @param item The item to check
     * @return If the item is a forbidden item
     */
    public static boolean isForbiddenItem(ItemStack item) {
        return getCustomItemId(item) != null && getCustomItemId(item).equals("forbidden");
    }

    /**
     * Gets the custom item id of an item
     *
     * @param item The item to get the id from
     * @return The custom item id
     */
    @Nullable
    public static String getCustomItemId(ItemStack item) {
        if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING)) return null;
        else return item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Gets the head of an offline player
     *
     * @param offlinePlayer The offline player
     * @return The head of the player
     */
    public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || offlinePlayer.getName() == null) return new CustomItem(Material.SKELETON_SKULL).setName("&dUnknown").setLore(new ArrayList<>(List.of("&8" + UUID.randomUUID()))).getItemStack();

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

    /**
     * Gets a skeleton skull instead of a head
     *
     * @param uuid The uuid of the bedrock player
     * @return A skeleton skull
     */
    public static ItemStack getBedrockPlayerHead(UUID uuid) {
        ItemStack head = new ItemStack(Material.SKELETON_SKULL);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        skullMeta.displayName(Component.text("§d" + LifeStealZ.getInstance().getGeyserManager().getOfflineBedrockPlayerName(uuid)));

        List<Component> lines = new ArrayList<>();
        lines.add(MessageUtils.getAndFormatMsg(false, "messages.revivePlayerDesc", "&7Click to revive this player"));
        lines.add(MessageUtils.formatMsg("<dark_gray>" + uuid));
        lines.add(MessageUtils.formatMsg("<dark_gray><i>This player is using the Bedrock Edition of Minecraft.</i>"));

        skullMeta.lore(lines);

        head.setItemMeta(skullMeta);
        return head;
    }

    /**
     * Gets the custom item data of an item
     *
     * @param itemId The id of the item
     * @return The custom item data
     */
    public static CustomItemData getCustomItemData(String itemId) {
        try {
            return new CustomItemData(itemId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
