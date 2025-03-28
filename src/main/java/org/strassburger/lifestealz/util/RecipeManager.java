package org.strassburger.lifestealz.util;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.customitems.CustomItem;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RecipeManager {
    private final LifeStealZ plugin;
    private final Map<Inventory, List<Integer>> animationMap = new HashMap<>();

    public RecipeManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves that an animation was started in an inventory to stop them when the inventory is closed
     * @param inventory The inventory to save the animation for
     * @param taskId The task id of the animation
     */
    private void addAnimation(Inventory inventory, int taskId) {
        if (animationMap.containsKey(inventory)) animationMap.get(inventory).add(taskId);
        else animationMap.put(inventory, new ArrayList<>(Collections.singletonList(taskId)));
    }

    /**
     * Cancels all animations for an inventory
     * @param inventory The inventory to cancel the animations for
     */
    public void cancelAnimations(Inventory inventory) {
        if (animationMap.containsKey(inventory)) {
            for (int taskId : animationMap.get(inventory)) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            animationMap.remove(inventory);
        }
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
        NamespacedKey recipeKey = new NamespacedKey(plugin, "recipe" + itemId);
        Bukkit.removeRecipe(recipeKey);
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
     * Returns a tag from a string
     * @param tagName The name of the tag (without the #)
     * @return The tag
     */
    private Tag<Material> tagFromString(String tagName) {
        Tag<Material> blockTag = Bukkit.getTag("blocks", NamespacedKey.minecraft(tagName), Material.class);
        if (blockTag != null) return blockTag;
        else return Bukkit.getTag("items", NamespacedKey.minecraft(tagName), Material.class);
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

        if (getRecipeIds().contains(material.toLowerCase())) {
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
     * Renders an ingredient in an inventory gui
     * @param inventory The inventory to render the ingredient in
     * @param slot The slot to render the ingredient in
     * @param material The material of the ingredient
     */
    private void renderIngredient(Inventory inventory, int slot, String material) {
        if (material == null || material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("empty")) return;

        if (material.startsWith("#") && tagFromString(material.substring(1)) != null) {
            Tag<Material> tag = tagFromString(material.substring(1).toLowerCase());
            Set<Material> materials = tag.getValues();
            startTagAnimation(inventory, slot, materials);
            return;
        }

        if (getRecipeIds().contains(material.toLowerCase())) {
            inventory.setItem(slot, new CustomItem(CustomItemManager.createCustomItem(material)).makeForbidden().getItemStack());
            return;
        }

        if (Material.getMaterial(material.toUpperCase()) != null) {
            inventory.setItem(slot, new CustomItem(new ItemStack(Material.valueOf(material.toUpperCase()), 1)).makeForbidden().getItemStack());
            return;
        }

        throw new IllegalArgumentException("Invalid material: " + material);
    }

    private void startTagAnimation(Inventory inventory, int slot, Set<Material> materials) {
        List<Material> materialList = new ArrayList<>(materials);
        AtomicReference<Integer> index = new AtomicReference<>(0);

        if (materialList.isEmpty()) return;

        Runnable runnable = () -> {
            int currentIndex = index.get();
            inventory.setItem(slot, new CustomItem(materialList.get(currentIndex)).makeForbidden().getItemStack());

            // Update index, loop back when reaching the end
            index.set((currentIndex + 1) % materialList.size());
        };

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 0L, 20L);

        if (taskId == -1) return;

        addAnimation(inventory, taskId);

        // Cancel the task after 30 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getScheduler().cancelTask(taskId);
            if (inventory != null) inventory.setItem(slot, new CustomItem(materialList.get(0)).makeForbidden().getItemStack());
        }, 20 * 30);
    }
}
