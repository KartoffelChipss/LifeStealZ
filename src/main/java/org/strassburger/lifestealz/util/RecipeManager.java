package org.strassburger.lifestealz.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    public RecipeManager() {}

    public static Set<String> getRecipeIds() {
        return Objects.requireNonNull(LifeStealZ.getInstance().getConfig().getConfigurationSection("items")).getKeys(false);
    }

    public static void registerRecipes() {
        for (String itemId : Objects.requireNonNull(LifeStealZ.getInstance().getConfig().getConfigurationSection("items")).getKeys(false)) {
            registerRecipe(itemId);
        }
    }

    public static boolean isCraftable(String itemId) {
        return LifeStealZ.getInstance().getConfig().getBoolean("items." + itemId + ".craftable");
    }

    private static void registerRecipe(String itemId) {
        boolean craftable = LifeStealZ.getInstance().getConfig().getBoolean("items." + itemId + ".craftable");

        if (!craftable) return;

        NamespacedKey heartRecipeKey = new NamespacedKey(LifeStealZ.getInstance(), "recipe" + itemId);
        ItemStack resultItem = CustomItemManager.createCustomItem(itemId);
        ShapedRecipe recipe = new ShapedRecipe(heartRecipeKey, resultItem);

        recipe.shape("ABC", "DEF", "GHI");
        List<String> rowOne = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".recipe.rowOne");
        List<String> rowTwo = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".recipe.rowTwo");
        List<String> rowThree = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".recipe.rowThree");

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

    public static void renderRecipe(Player player, String itemId) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, MessageUtils.formatMsg("&8Crafting recipe"));

        inventory.setItem(40, CustomItemManager.createCloseItem());

        ItemStack glass = new CustomItem(Material.GRAY_STAINED_GLASS_PANE)
                .setName("&c ")
                .getItemStack();

        List<Integer> glassSlots = Arrays.asList(0,1,2,3,4,5,6,7,8,9,13,14,15,16,17,18,22,23,25,26,27,31,32,33,34,35,36,37,38,39,41,42,43,44);
        for (int slot : glassSlots) {
            inventory.setItem(slot, glass);
        }

        List<String> rowOne = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".recipe.rowOne");
        List<String> rowTwo = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".recipe.rowTwo");
        List<String> rowThree = LifeStealZ.getInstance().getConfig().getStringList("items." + itemId + ".recipe.rowThree");
        renderIngredient(inventory, 10, rowOne.get(0));
        renderIngredient(inventory, 11, rowOne.get(1));
        renderIngredient(inventory, 12, rowOne.get(2));
        renderIngredient(inventory, 19, rowTwo.get(0));
        renderIngredient(inventory, 20, rowTwo.get(1));
        renderIngredient(inventory, 21, rowTwo.get(2));
        renderIngredient(inventory, 28, rowThree.get(0));
        renderIngredient(inventory, 29, rowThree.get(1));
        renderIngredient(inventory, 30, rowThree.get(2));
        inventory.setItem(24, CustomItemManager.createCustomItem(itemId));

        GuiManager.RECIPE_GUI_MAP.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    private static void setIngredient(ShapedRecipe recipe, String key, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) return;
        if (getRecipeIds().contains(material.toLowerCase())) recipe.setIngredient(key.charAt(0), CustomItemManager.createCustomItem(material));
        else if (Material.getMaterial(material) != null) recipe.setIngredient(key.charAt(0), Material.valueOf(material));
        else throw new IllegalArgumentException("Invalid material: " + material);
    }

    private static void renderIngredient(Inventory inventory, int slot, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) return;
        if (getRecipeIds().contains(material.toLowerCase())) inventory.setItem(slot, CustomItemManager.createCustomItem(material));
        else if (Material.getMaterial(material) != null) inventory.setItem(slot, new ItemStack(Material.valueOf(material), 1));
        else throw new IllegalArgumentException("Invalid material: " + material);
    }
}
