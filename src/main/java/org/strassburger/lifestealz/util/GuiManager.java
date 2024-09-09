package org.strassburger.lifestealz.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiManager {
    public static Map<UUID, Inventory> REVIVE_GUI_MAP = new HashMap<>();
    public static Map<UUID, Inventory> RECIPE_GUI_MAP = new HashMap<>();

    public static void openReviveGui(Player player, int page) {
        List<UUID> eliminatedPlayers = LifeStealZ.getInstance().getStorage().getEliminatedPlayers();

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, MessageUtils.getAndFormatMsg(false, "messages.reviveTitle", "&8Revive a player"));

        int itemsPerPage = 5 * 9;

        if (page < 1) page = 1;

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(page * itemsPerPage, eliminatedPlayers.size());

        for (int i = startIndex; i < endIndex; i++) {
            UUID eliminatedPlayerUUID = eliminatedPlayers.get(i);
            if (eliminatedPlayerUUID == null) continue;
            if(!LifeStealZ.getInstance().getGeyserPlayerFile().isPlayerStored(eliminatedPlayerUUID)) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(eliminatedPlayerUUID);
                inventory.addItem(CustomItemManager.getPlayerHead(offlinePlayer));
            } else {
                inventory.addItem(CustomItemManager.getBedrockPlayerHead(eliminatedPlayerUUID));
            }
        }

        addNavbar(inventory, page, page > 1, endIndex < eliminatedPlayers.size());

        player.openInventory(inventory);
        GuiManager.REVIVE_GUI_MAP.put(player.getUniqueId(), inventory);
    }

    private static void addNavbar(Inventory inventory, int page, boolean addBackButton, boolean addNextButton) {
        inventory.setItem(49, CustomItemManager.createCloseItem());

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.text("§r "));
        glass.setItemMeta(glassMeta);
        int[] glassSlots = {45, 47, 48, 50, 51, 53};
        for (int slot : glassSlots) {
            inventory.setItem(slot, glass);
        }

        if (addBackButton) inventory.setItem(46, CustomItemManager.createBackItem(page - 1));
        else inventory.setItem(46, glass);

        if (addNextButton) inventory.setItem(52, CustomItemManager.createNextItem(page + 1));
        else inventory.setItem(52, glass);
    }
}
