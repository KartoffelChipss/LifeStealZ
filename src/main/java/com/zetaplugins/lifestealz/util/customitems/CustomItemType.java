package com.zetaplugins.lifestealz.util.customitems;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static com.zetaplugins.lifestealz.util.customitems.CustomItemManager.CUSTOM_ITEM_TYPE_KEY;

/**
 * Represents the type of a custom item in the LifeStealZ plugin.
 */
public enum CustomItemType {
    HEART("heart"),
    REVIVE("revive"),
    REVIVE_BEACON("revivebeacon"),
    NONE("none"),
    NONUSABLE("non-usable");

    private final String type;

    /**
     * Creates a new CustomItemType with the given type string.
     * @param type the string representation of the custom item type
     */
    CustomItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Checks if the given ItemStack is of this CustomItemType.
     * @param item the ItemStack to check
     * @return true if the ItemStack is of this CustomItemType, false otherwise
     */
    public boolean is(ItemStack item) {
        try {
            return item.getItemMeta() != null
                    && item.getItemMeta().getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING)
                    && item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_KEY, PersistentDataType.STRING).equalsIgnoreCase(getType());
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Returns the CustomItemType corresponding to the given string.
     * @param type the string representation of the custom item type
     * @return the CustomItemType corresponding to the string, or NONE if not found
     */
    public static CustomItemType fromString(String type) {
        for (CustomItemType itemType : CustomItemType.values()) {
            if (itemType.getType().equalsIgnoreCase(type)) {
                return itemType;
            }
        }
        return NONE;
    }

    /**
     * Returns the CustomItemType for the given custom item ID from the configuration.
     * @param customItemId the ID of the custom item
     * @param config the configuration file containing custom item definitions
     * @return the CustomItemType for the given custom item ID, or NONE if not found
     */
    public static CustomItemType fromCustomItem(String customItemId, FileConfiguration config) {
        String type = config.getString(customItemId + ".customItemType", "none");
        return fromString(type);
    }
}
