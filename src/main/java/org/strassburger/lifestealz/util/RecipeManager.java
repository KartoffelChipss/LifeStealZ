package org.strassburger.lifestealz.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.customitems.CustomItem;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RecipeManager {
    private final LifeStealZ plugin;

    public RecipeManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns all recipe ids
     * @return All recipe ids
     */
    public Set<String> getRecipeIds() {
        return plugin.getConfigManager().getCustomItemConfig().getKeys(false);
    }

    /**
     * Registers all recipes
     */
    public void registerRecipes() {
        for (String itemId : plugin.getConfigManager().getCustomItemConfig().getKeys(false)) {
            removeRecipe(itemId);
            registerRecipe(itemId);
        }
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
     * Registers a recipe
     * @param itemId The item id to register
     */
    private void registerRecipe(String itemId) {
        boolean craftable = plugin.getConfigManager().getCustomItemConfig().getBoolean(itemId + ".craftable");

        if (!craftable) return;

        FileConfiguration config = plugin.getConfigManager().getCustomItemConfig();

        NamespacedKey heartRecipeKey = new NamespacedKey(plugin, "recipe" + itemId);
        ItemStack resultItem = CustomItemManager.createCustomItem(itemId);
        ShapedRecipe recipe = new ShapedRecipe(heartRecipeKey, resultItem);

        recipe.shape("ABC", "DEF", "GHI");
        List<String> rowOne = config.getStringList(itemId + ".recipe.rowOne");
        List<String> rowTwo = config.getStringList(itemId + ".recipe.rowTwo");
        List<String> rowThree = config.getStringList(itemId + ".recipe.rowThree");

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
        NamespacedKey heartRecipeKey = new NamespacedKey(plugin, "recipe" + itemId);
        Bukkit.removeRecipe(heartRecipeKey);
    }

    /**
     * Renders a recipe in an inventory gui
     * @param player The player to render the recipe for
     * @param itemId The item id to render
     */
    public void renderRecipe(Player player, String itemId) {
        FileConfiguration config = plugin.getConfigManager().getCustomItemConfig();
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, MessageUtils.formatMsg("&8Crafting recipe"));

        inventory.setItem(40, CustomItemManager.createCloseItem());

        ItemStack glass = new CustomItem(Material.GRAY_STAINED_GLASS_PANE)
                .setName("&c ")
                .makeForbidden()
                .getItemStack();

        List<Integer> glassSlots = Arrays.asList(0,1,2,3,4,5,6,7,8,9,13,14,15,16,17,18,22,23,25,26,27,31,32,33,34,35,36,37,38,39,41,42,43,44);
        for (int slot : glassSlots) {
            inventory.setItem(slot, glass);
        }

        List<String> rowOne = config.getStringList(itemId + ".recipe.rowOne");
        List<String> rowTwo = config.getStringList(itemId + ".recipe.rowTwo");
        List<String> rowThree = config.getStringList(itemId + ".recipe.rowThree");
        renderIngredient(inventory, 10, rowOne.get(0));
        renderIngredient(inventory, 11, rowOne.get(1));
        renderIngredient(inventory, 12, rowOne.get(2));
        renderIngredient(inventory, 19, rowTwo.get(0));
        renderIngredient(inventory, 20, rowTwo.get(1));
        renderIngredient(inventory, 21, rowTwo.get(2));
        renderIngredient(inventory, 28, rowThree.get(0));
        renderIngredient(inventory, 29, rowThree.get(1));
        renderIngredient(inventory, 30, rowThree.get(2));
        inventory.setItem(24, new CustomItem(CustomItemManager.createCustomItem(itemId)).makeForbidden().getItemStack());

        GuiManager.RECIPE_GUI_MAP.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    /**
     * Sets an ingredient for a recipe
     * @param recipe The recipe to set the ingredient for
     * @param key The key of the ingredient
     * @param material The material of the ingredient
     */
    private void setIngredient(ShapedRecipe recipe, String key, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) return;
        if (getRecipeIds().contains(material.toLowerCase())) recipe.setIngredient(key.charAt(0), CustomItemManager.createCustomItem(material));
        else if (Material.getMaterial(material) != null) recipe.setIngredient(key.charAt(0), Material.valueOf(material));
        else throw new IllegalArgumentException("Invalid material: " + material);
    }

    /**
     * Renders an ingredient in an inventory gui
     * @param inventory The inventory to render the ingredient in
     * @param slot The slot to render the ingredient in
     * @param material The material of the ingredient
     */
    private void renderIngredient(Inventory inventory, int slot, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) return;
        if (getRecipeIds().contains(material.toLowerCase())) inventory.setItem(slot, new CustomItem(CustomItemManager.createCustomItem(material)).makeForbidden().getItemStack());
        else if (Material.getMaterial(material) != null) inventory.setItem(slot, new CustomItem(new ItemStack(Material.valueOf(material), 1)).makeForbidden().getItemStack());
        else throw new IllegalArgumentException("Invalid material: " + material);
    }
}
