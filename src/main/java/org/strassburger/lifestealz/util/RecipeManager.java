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

        recipe.setIngredient('A', Material.valueOf(rowOne.get(0)));
        recipe.setIngredient('B', Material.valueOf(rowOne.get(1)));
        recipe.setIngredient('C', Material.valueOf(rowOne.get(2)));
        recipe.setIngredient('D', Material.valueOf(rowTwo.get(0)));
        recipe.setIngredient('E', Material.valueOf(rowTwo.get(1)));
        recipe.setIngredient('F', Material.valueOf(rowTwo.get(2)));
        recipe.setIngredient('G', Material.valueOf(rowThree.get(0)));
        recipe.setIngredient('H', Material.valueOf(rowThree.get(1)));
        recipe.setIngredient('I', Material.valueOf(rowThree.get(2)));

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
        inventory.setItem(10, new ItemStack(Material.valueOf(rowOne.get(0)), 1));
        inventory.setItem(11, new ItemStack(Material.valueOf(rowOne.get(1)), 1));
        inventory.setItem(12, new ItemStack(Material.valueOf(rowOne.get(2)), 1));
        inventory.setItem(19, new ItemStack(Material.valueOf(rowTwo.get(0)), 1));
        inventory.setItem(20, new ItemStack(Material.valueOf(rowTwo.get(1)), 1));
        inventory.setItem(21, new ItemStack(Material.valueOf(rowTwo.get(2)), 1));
        inventory.setItem(28, new ItemStack(Material.valueOf(rowThree.get(0)), 1));
        inventory.setItem(29, new ItemStack(Material.valueOf(rowThree.get(1)), 1));
        inventory.setItem(30, new ItemStack(Material.valueOf(rowThree.get(2)), 1));
        inventory.setItem(24, CustomItemManager.createCustomItem(itemId));

        GuiManager.RECIPE_GUI_MAP.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }
}
