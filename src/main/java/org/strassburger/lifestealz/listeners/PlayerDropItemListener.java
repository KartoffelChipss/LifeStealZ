package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import static org.strassburger.lifestealz.util.customitems.CustomItemManager.NO_DESPAWN_KEY;

public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(NO_DESPAWN_KEY)) item.setUnlimitedLifetime(true);
    }
}
