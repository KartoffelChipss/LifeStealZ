package org.strassburger.lifestealz.util.customblocks;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;

public enum CustomBlock {
    REVIVE_BEACON("revive_beacon");

    private final static NamespacedKey CUSTOM_BLOCK_KEY = new NamespacedKey("lifestealz", "custom_block");
    private final static NamespacedKey CUSTOM_ITEM_ID_KEY = new NamespacedKey("lifestealz", "custom_item_id_block");

    private final String key;

    /**
     * Constructor for CustomBlock enum
     * @param key The unique key for the custom block
     */
    CustomBlock(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Tags a block with the custom block key
     * @param block The block to tag
     */
    public void make(Block block) {
        BlockTagManager.tagBlock(
                block,
                CUSTOM_BLOCK_KEY,
                PersistentDataType.STRING,
                this.getKey()
        );
    }

    /**
     * Tags a block with the custom block key and a custom item ID
     * @param block The block to tag
     * @param customItemId The custom item ID to associate with the block
     */
    public void make(Block block, String customItemId) {
        BlockTagManager.tagBlock(
                block,
                CUSTOM_BLOCK_KEY,
                PersistentDataType.STRING,
                this.getKey()
        );

        BlockTagManager.tagBlock(
                block,
                CUSTOM_ITEM_ID_KEY,
                PersistentDataType.STRING,
                customItemId
        );
    }

    /**
     * Checks if a block is tagged with this custom block key
     * @param block The block to check
     * @return true if the block is tagged with this custom block key, false otherwise
     */
    public boolean is(Block block) {
        String blockKey = BlockTagManager.getBlockTag(block, CUSTOM_BLOCK_KEY, PersistentDataType.STRING);
        return blockKey != null && blockKey.equalsIgnoreCase(this.getKey());
    }

    /**
     * Retrieves the custom item ID associated with a block
     * @param block The block to retrieve the custom item ID from
     * @return The custom item ID, or null if not found
     */
    public String getCustomItemId(Block block) {
        return BlockTagManager.getBlockTag(block, CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Retrieves a CustomBlock from its key
     * @param key The key of the custom block to retrieve
     * @return The CustomBlock associated with the key, or null if not found
     */
    public static CustomBlock fromKey(String key) {
        for (CustomBlock customBlock : values()) {
            if (customBlock.getKey().equalsIgnoreCase(key)) {
                return customBlock;
            }
        }
        return null;
    }
}
