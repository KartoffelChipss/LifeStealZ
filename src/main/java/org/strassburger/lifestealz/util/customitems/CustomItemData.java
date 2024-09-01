package org.strassburger.lifestealz.util.customitems;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.List;

public class CustomItemData {
    private final String itemId;
    private final String name;
    private final List<String> lore;
    private final Material material;
    private final boolean enchanted;
    private final int customModelData;
    private final String customItemType;
    private final int customHeartValue;
    private final boolean craftable;

    public CustomItemData(String itemId, String name, List<String> lore, Material material, boolean enchanted, int customModelData, String customItemType, int customHeartValue, boolean craftable) {
        this.itemId = itemId;
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.enchanted = enchanted;
        this.customModelData = customModelData;
        this.customItemType = customItemType;
        this.customHeartValue = customHeartValue;
        this.craftable = craftable;
    }

    public CustomItemData(String itemId) {
        FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
        this.itemId = itemId;
        this.name = config.getString("items." + itemId + ".name");
        this.lore = config.getStringList("items." + itemId + ".lore");
        this.material = Material.valueOf(config.getString("items." + itemId + ".material"));
        this.enchanted = config.getBoolean("items." + itemId + ".enchanted");
        this.customModelData = config.getInt("items." + itemId + ".customModelData");
        this.customItemType = config.getString("items." + itemId + ".customItemType");
        this.customHeartValue = config.getInt("items." + itemId + ".customHeartValue");
        this.craftable = config.getBoolean("items." + itemId + ".craftable");
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

    public boolean isCraftable() {
        return craftable;
    }

    public static class CustomItemSoundData {
        private final boolean enabled;
        private final Sound sound;
        private final double volume;
        private final double pitch;

        private CustomItemSoundData(String itemId) {
            FileConfiguration config = LifeStealZ.getInstance().getConfigManager().getCustomItemConfig();
            enabled = config.getBoolean("items." + itemId + ".sound.enabled");
            sound = Sound.valueOf(config.getString("items." + itemId + ".sound.sound"));
            volume = config.getDouble("items." + itemId + ".sound.volume");
            pitch = config.getDouble("items." + itemId + ".sound.pitch");
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