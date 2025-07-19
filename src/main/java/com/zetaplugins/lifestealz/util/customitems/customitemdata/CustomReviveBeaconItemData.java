package com.zetaplugins.lifestealz.util.customitems.customitemdata;

import org.bukkit.Material;
import com.zetaplugins.lifestealz.util.customblocks.ParticleColor;

import java.util.Objects;

public final class CustomReviveBeaconItemData extends CustomItemData {
    private final int reviveTime;
    private final boolean allowBreakingBeaconWhileReviving;
    private final boolean showLaser;
    private final Material innerLaser;
    private final Material outerLaser;
    private final boolean showParticleRing;
    private final ParticleColor particleColor;
    private final boolean showEnchantParticles;
    private final Material decoyMaterial;

    public CustomReviveBeaconItemData(String itemId) throws IllegalArgumentException {
        super(itemId);
        this.reviveTime = getConfigurationSection().getInt("reviveTime", 30);
        this.allowBreakingBeaconWhileReviving = getConfigurationSection().getBoolean("allowBreakingBeaconWhileReviving", true);
        this.showLaser = getConfigurationSection().getBoolean("showLaser", true);
        this.innerLaser = parseMaterial(
                Objects.requireNonNullElse(getConfigurationSection().getString("innerLaserMaterial"), "RED_GLAZED_TERRACOTTA"),
                Material.RED_GLAZED_TERRACOTTA
        );
        this.outerLaser = parseMaterial(
                Objects.requireNonNullElse(getConfigurationSection().getString("outerLaserMaterial"), "RED_STAINED_GLASS"),
                Material.RED_STAINED_GLASS
        );
        this.showParticleRing = getConfigurationSection().getBoolean("showParticleRing", true);
        this.particleColor = ParticleColor.fromString(
                getConfigurationSection().getString("particleColor", "RED")
        );
        this.showEnchantParticles = getConfigurationSection().getBoolean("showEnchantParticles", true);
        this.decoyMaterial = parseMaterial(
                getConfigurationSection().getString("decoyMaterial", "RED_STAINED_GLASS"),
                Material.RED_STAINED_GLASS
        );
    }

    public int getReviveTime() {
        return reviveTime;
    }

    public boolean isAllowBreakingBeaconWhileReviving() {
        return allowBreakingBeaconWhileReviving;
    }

    public boolean shouldShowLaser() {
        return showLaser;
    }

    public boolean shouldShowParticleRing() {
        return showParticleRing;
    }

    public boolean shouldShowEnchantParticles() {
        return showEnchantParticles;
    }

    public Material getDecoyMaterial() {
        return decoyMaterial;
    }

    public Material getInnerLaser() {
        return innerLaser;
    }

    public Material getOuterLaser() {
        return outerLaser;
    }

    public ParticleColor getParticleColor() {
        return particleColor;
    }

    /**
     * Parses a material from a string, returning a fallback material if the string is invalid.
     * @param materialName the name of the material to parse
     * @param fallbackMaterial the material to return if the string is invalid
     * @return the parsed material, or the fallback material if the string is invalid
     */
    private Material parseMaterial(String materialName, Material fallbackMaterial) {
        Material material = Material.getMaterial(materialName.toUpperCase());
        return material != null ? material : fallbackMaterial;
    }
}
