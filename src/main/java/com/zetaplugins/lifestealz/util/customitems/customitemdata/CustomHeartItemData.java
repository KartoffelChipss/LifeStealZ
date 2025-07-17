package com.zetaplugins.lifestealz.util.customitems.customitemdata;

import org.bukkit.configuration.ConfigurationSection;

public final class CustomHeartItemData extends CustomItemData {
    private final int customHeartValue;
    private final int minHearts;
    private final int maxHearts;

    public CustomHeartItemData(String itemId) throws IllegalArgumentException {
        super(itemId);
        ConfigurationSection section = getConfigurationSection();
        this.customHeartValue = section.getInt("customHeartValue", 1);
        this.minHearts = section.getInt("minHearts", 0);
        this.maxHearts = section.getInt("maxHearts", -1);
    }

    public int getCustomHeartValue() {
        return customHeartValue;
    }

    public int getMinHearts() {
        return minHearts;
    }

    public int getMaxHearts() {
        return maxHearts;
    }
}
