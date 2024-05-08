package org.strassburger.lifestealz.util.customitems;

import org.bukkit.Material;
import org.bukkit.Sound;
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
        this.itemId = itemId;
        this.name = LifeStealZ.getInstance().getConfig().getString("items." + itemId + ".name");
        this.lore = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".lore");
        this.material = Material.valueOf(LifeStealZ.getInstance().getConfig().getString("items." + itemId + ".material"));
        this.enchanted = LifeStealZ.getInstance().getConfig().getBoolean("items." + itemId + ".enchanted");
        this.customModelData = LifeStealZ.getInstance().getConfig().getInt("items." + itemId + ".customModelData");
        this.customItemType = LifeStealZ.getInstance().getConfig().getString("items." + itemId + ".customItemType");
        this.customHeartValue = LifeStealZ.getInstance().getConfig().getInt("items." + itemId + ".customHeartValue");
        this.craftable = LifeStealZ.getInstance().getConfig().getBoolean("items." + itemId + ".craftable");
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
            enabled = LifeStealZ.getInstance().getConfig().getBoolean("items." + itemId + ".sound.enabled");
            sound = Sound.valueOf(LifeStealZ.getInstance().getConfig().getString("items." + itemId + ".sound.sound"));
            volume = LifeStealZ.getInstance().getConfig().getDouble("items." + itemId + ".sound.volume");
            pitch = LifeStealZ.getInstance().getConfig().getDouble("items." + itemId + ".sound.pitch");
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