package com.zetaplugins.lifestealz.util.customitems.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.zetaplugins.lifestealz.LifeStealZ;

import java.util.*;

public final class RecipeManager {
    private final LifeStealZ plugin;
    private final RecipeRenderer recipeRenderer;
    private final RecipeRegistrar recipeRegistrar;

    public RecipeManager(LifeStealZ plugin) {
        this.plugin = plugin;
        this.recipeRenderer = new RecipeRenderer(plugin);
        this.recipeRegistrar = new RecipeRegistrar(plugin);
    }

    /**
     * Returns all recipe ids
     * @return All recipe ids
     */
    public Set<String> getItemIds() {
        return plugin.getConfigManager().getCustomItemConfig().getKeys(false);
    }

    /**
     * Returns all recipe ids for an item
     * @param itemId The item id
     * @return All recipe ids for the item
     */
    public Set<String> getRecipeIds(String itemId) {
        Set<String> recipeIds = new HashSet<>();
        FileConfiguration config = plugin.getConfigManager().getCustomItemConfig();
        if (!config.isSet(itemId + ".recipes")) return recipeIds;

        ConfigurationSection recipeSection = config.getConfigurationSection(itemId + ".recipes");
        if (recipeSection == null) return recipeIds;

        recipeIds.addAll(recipeSection.getKeys(false));

        return recipeIds;
    }

    /**
     * Registers all recipes
     */
    public void registerRecipes() {
        recipeRegistrar.registerRecipes();
    }

    /**
     * Checks if an item is craftable
     * @param itemId The item id
     * @return If the item is craftable
     */
    public boolean isCraftable(String itemId) {
        return plugin.getConfigManager().getCustomItemConfig().getBoolean(itemId + ".craftable");
    }

    /**
     * Renders a recipe in an inventory gui
     * @param player The player to render the recipe for
     * @param itemId The item id to render
     */
    public void renderRecipe(Player player, String itemId) {
        recipeRenderer.renderRecipe(player, itemId);
    }

    /**
     * Renders a recipe in an inventory gui
     * @param player The player to render the recipe for
     * @param itemId The item id to render
     * @param recipeId The recipe id to render
     */
    public void renderRecipe(Player player, String itemId, String recipeId) {
        recipeRenderer.renderRecipe(player, itemId, recipeId);
    }

    /**
     * Cancels all animations for an inventory
     * @param inventory The inventory to cancel the animations for
     */
    public void cancelAnimations(Inventory inventory) {
        recipeRenderer.cancelAnimations(inventory);
    }
}
