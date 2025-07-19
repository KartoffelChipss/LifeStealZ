package com.zetaplugins.lifestealz.util.customitems.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.util.customitems.CustomItemManager;

import java.util.List;
import java.util.Set;

final class RecipeRegistrar {
    private final LifeStealZ plugin;

    public RecipeRegistrar(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers all recipes
     */
    public void registerRecipes() {
        for (String itemId : plugin.getConfigManager().getCustomItemConfig().getKeys(false)) {
            removeRecipe(itemId);
            registerCustomItemRecipes(itemId);
        }
    }

    /**
     * Registers a recipe
     * @param itemId The item id to register
     */
    private void registerCustomItemRecipes(String itemId) {
        boolean craftable = plugin.getConfigManager().getCustomItemConfig().getBoolean(itemId + ".craftable");

        if (!craftable) return;

        FileConfiguration config = plugin.getConfigManager().getCustomItemConfig();

        boolean hasOneRecipe = config.isSet(itemId + ".recipe");

        if (hasOneRecipe) {
            List<String> rowOne = config.getStringList(itemId + ".recipe.rowOne");
            List<String> rowTwo = config.getStringList(itemId + ".recipe.rowTwo");
            List<String> rowThree = config.getStringList(itemId + ".recipe.rowThree");

            registerRecipe(itemId, "default", rowOne, rowTwo, rowThree);
        } else {
            ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
            if (recipeSection == null) return;

            for (String recipeId : recipeSection.getKeys(false)) {
                List<String> rowOne = recipeSection.getStringList(recipeId + ".rowOne");
                List<String> rowTwo = recipeSection.getStringList(recipeId + ".rowTwo");
                List<String> rowThree = recipeSection.getStringList(recipeId + ".rowThree");

                registerRecipe(itemId, recipeId, rowOne, rowTwo, rowThree);
            }
        }
    }

    private void registerRecipe(String itemId, String recipeId, List<String> rowOne, List<String> rowTwo, List<String> rowThree) {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "recipe_" + itemId + "_" + recipeId);
        ItemStack resultItem = CustomItemManager.createCustomItem(itemId);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, resultItem);

        recipe.shape("ABC", "DEF", "GHI");

        setIngredient(recipe, "A", rowOne.get(0));
        setIngredient(recipe, "B", rowOne.get(1));
        setIngredient(recipe, "C", rowOne.get(2));
        setIngredient(recipe, "D", rowTwo.get(0));
        setIngredient(recipe, "E", rowTwo.get(1));
        setIngredient(recipe, "F", rowTwo.get(2));
        setIngredient(recipe, "G", rowThree.get(0));
        setIngredient(recipe, "H", rowThree.get(1));
        setIngredient(recipe, "I", rowThree.get(2));

        Bukkit.addRecipe(recipe);
    }

    /**
     * Removes a recipe
     * @param itemId The id of the item to remove the recipe for
     */
    private void removeRecipe(String itemId) {
        FileConfiguration config = plugin.getConfigManager().getCustomItemConfig();
        boolean hasOneRecipe = config.isSet(itemId + ".recipe");

        if (hasOneRecipe) {
            NamespacedKey recipeKey = new NamespacedKey(plugin, "recipe_" + itemId + "_default");
            Bukkit.removeRecipe(recipeKey);
        } else {
            ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
            if (recipeSection == null) return;

            for (String recipeId : recipeSection.getKeys(false)) {
                NamespacedKey recipeKey = new NamespacedKey(plugin, "recipe_" + itemId + "_" + recipeId);
                Bukkit.removeRecipe(recipeKey);
            }
        }
    }

    /**
     * Sets an ingredient for a recipe
     * @param recipe The recipe to set the ingredient for
     * @param key The key of the ingredient
     * @param material The material of the ingredient
     */
    private void setIngredient(ShapedRecipe recipe, String key, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) return;

        if (material.startsWith("#") && tagFromString(material.substring(1)) != null) {
            Tag<Material> tag = tagFromString(material.substring(1).toLowerCase());
            recipe.setIngredient(key.charAt(0), new RecipeChoice.MaterialChoice(tag));
            return;
        }

        if (getItemIds().contains(material.toLowerCase())) {
            recipe.setIngredient(key.charAt(0), CustomItemManager.createCustomItem(material));
            return;
        }

        if (Material.getMaterial(material.toUpperCase()) != null) {
            recipe.setIngredient(key.charAt(0), Material.valueOf(material.toUpperCase()));
            return;
        }

        throw new IllegalArgumentException("Invalid material or Tag: " + material + " (" + material.toUpperCase() + ")");
    }

    /**
     * Returns a tag from a string
     * @param tagName The name of the tag (without the #)
     * @return The tag
     */
    private Tag<Material> tagFromString(String tagName) {
        Tag<Material> blockTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName), Material.class);
        if (blockTag != null) return blockTag;
        else return Bukkit.getTag("items", NamespacedKey.minecraft(tagName), Material.class);
    }

    private Set<String> getItemIds() {
        return plugin.getConfigManager().getCustomItemConfig().getKeys(false);
    }
}
