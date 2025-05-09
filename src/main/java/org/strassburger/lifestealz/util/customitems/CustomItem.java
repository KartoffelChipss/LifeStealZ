package org.strassburger.lifestealz.util.customitems;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.strassburger.lifestealz.util.customitems.CustomItemManager.CUSTOM_ITEM_ID_KEY;

public final class CustomItem {
    private final ItemStack itemStack;

    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public CustomItem(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public CustomItem() {
        this.itemStack = new ItemStack(Material.AIR);
    }

    public CustomItem setMaterial(Material material) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        int amount = itemStack.getAmount();
        ItemStack newItemStack = new ItemStack(material, amount);
        newItemStack.setItemMeta(itemMeta);
        return this;
    }

    public CustomItem setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public CustomItem setName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(MessageUtils.formatMsg(name));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setName(Component name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(name);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setCustomModelID(int customModelID) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelID);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setEnchanted(boolean enchanted) {
		if (!enchanted) return this;

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem addFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem addEnchantment(Enchantment enchantment, int level) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public PersistentDataContainer getPersistentDataContainer() {
        return itemStack.getItemMeta().getPersistentDataContainer();
    }

    public CustomItem makeForbidden() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_ID_KEY, PersistentDataType.STRING, "forbidden");
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem setLore(List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> newLore = new ArrayList<>(lore.size());
        for (String s : lore) {
            newLore.add(MessageUtils.formatMsg(s));
        }
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public CustomItem addLore(String lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> newLore = new ArrayList<>(Objects.requireNonNull(itemMeta.lore()));
        newLore.add(MessageUtils.formatMsg(lore));
        itemMeta.lore(newLore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
