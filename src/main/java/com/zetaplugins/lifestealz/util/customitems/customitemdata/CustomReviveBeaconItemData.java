package com.zetaplugins.lifestealz.util.customitems.customitemdata;

import org.bukkit.Material;
import com.zetaplugins.lifestealz.util.customblocks.ParticleColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

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
    private final boolean showBossbar;
    private final String bossbarTitle;
    private final BarColor bossbarColor;
    private final BarStyle bossbarStyle;

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
        this.showBossbar = getConfigurationSection().getBoolean("showBossbar", false);
        this.bossbarTitle = getConfigurationSection().getString("bossbarText");
        this.bossbarColor = parseBarColor("bossbarColor", BarColor.RED);
        this.bossbarStyle = parseBarStyle("bossbarStyle", BarStyle.SOLID);
    }

    public int getReviveTime() {
        return reviveTime;
    }

    public String getBossbarTitle() {
        return bossbarTitle;
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

    public boolean shouldShowBossbar() {
        return showBossbar;
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

    public BarColor getBossbarColor() {
        return bossbarColor;
    }

    public BarStyle getBossbarStyle() {
        return bossbarStyle;
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

    /**
     * Parses a BarColor from a string, returning a fallback color if the string is invalid.
     * @param color the name of the color to parse
     * @param fallbackColor the color to return if the string is invalid
     * @return the parsed color, or the fallback color if the string is invalid
     */
    private BarColor parseBarColor(String color, BarColor fallbackColor) {
        BarColor barColor = BarColor.valueOf(color.toUpperCase());
        return barColor != null ? barColor : fallbackColor;
    }

    /**
     * Parses a BarStyle from a string, returning a fallback style if the string is invalid.
     * @param style the style to parse
     * @param fallbackStyle the style to return if the string is invalid
     * @return the parsed style, or the fallback style if the string is invalid
     */
    private BarStyle parseBarStyle(String style, BarStyle fallbackStyle) {
        BarStyle barStyle = BarStyle.valueOf(style.toUpperCase());
        return barStyle != null ? barStyle : fallbackStyle;
    }
}
