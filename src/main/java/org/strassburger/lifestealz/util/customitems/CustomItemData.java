package org.strassburger.lifestealz.util.customitems;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.List;

public final class CustomItemData {
    private final String itemId;
    private final String name;
    private final List<String> lore;
    private final Material material;
    private final boolean enchanted;
    private final int customModelData;
    private final String customItemType;
    private final int customHeartValue;
    private final int minHearts;
    private final int maxHearts;
    private final boolean craftable;
    private final boolean requirePermission;

    public CustomItemData(String itemId, String name, List<String> lore, Material material, boolean enchanted, int customModelData, String customItemType, int customHeartValue, int minHearts, int maxHearts, boolean craftable, boolean requirePermission) {
        this.itemId = itemId;
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.enchanted = enchanted;
        this.customModelData = customModelData;
        this.customItemType = customItemType;
        this.customHeartValue = customHeartValue;
        this.minHearts = minHearts;
        this.maxHearts = maxHearts;
        this.craftable = craftable;
        this.requirePermission = requirePermission;
    }

    public CustomItemData(String itemId) throws IllegalArgumentException {
        FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();

        ConfigurationSection section = config.getConfigurationSection(itemId);

        if (section == null) throw new IllegalArgumentException("Custom item with id " + itemId + " does not exist!");

        this.itemId = itemId;
        this.name = config.getString(itemId + ".name");
        this.lore = config.getStringList(itemId + ".lore");
        this.material = Material.valueOf(config.getString(itemId + ".material"));
        this.enchanted = config.getBoolean(itemId + ".enchanted");
        this.customModelData = config.getInt(itemId + ".customModelData");
        this.customItemType = config.getString(itemId + ".customItemType");
        this.customHeartValue = config.getInt(itemId + ".customHeartValue");
        this.minHearts = config.getInt(itemId + ".minHearts");
        this.maxHearts = config.getInt(itemId + ".maxHearts");
        this.craftable = config.getBoolean(itemId + ".craftable");
        this.requirePermission = config.getBoolean(itemId + ".requirePermission");
    }

    public CustomItemSoundData getSound() {
        return new CustomItemSoundData(itemId);
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isEnchanted() {
        return enchanted;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getCustomItemType() {
        return customItemType;
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

    public boolean isCraftable() {
        return craftable;
    }

    public boolean requiresPermission() {
        return requirePermission;
    }

    public String getPermission() {
        return "lifestealz.item." + itemId;
    }

    public static class CustomItemSoundData {
        private final boolean enabled;
        private final Sound sound;
        private final double volume;
        private final double pitch;

        private CustomItemSoundData(String itemId) {
            FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
            enabled = config.getBoolean(itemId + ".sound.enabled");
            sound = Sound.valueOf(config.getString(itemId + ".sound.sound"));
            volume = config.getDouble(itemId + ".sound.volume");
            pitch = config.getDouble(itemId + ".sound.pitch");
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Sound getSound() {
            return sound;
        }

        public double getVolume() {
            return volume;
        }

        public double getPitch() {
            return pitch;
        }
    }
}