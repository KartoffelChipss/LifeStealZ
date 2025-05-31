package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

import static org.strassburger.lifestealz.util.customitems.CustomItemManager.DESPAWNABLE_KEY;
import static org.strassburger.lifestealz.util.customitems.CustomItemManager.INVULNERABLE_KEY;

public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();

        if (CustomItemManager.isForbiddenItem(item.getItemStack())) {
            item.remove();
            return;
        }

        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(DESPAWNABLE_KEY) && !Boolean.TRUE.equals(container.get(DESPAWNABLE_KEY, PersistentDataType.BOOLEAN))) item.setUnlimitedLifetime(true);
        if (container.has(INVULNERABLE_KEY)) item.setInvulnerable(Boolean.TRUE.equals(container.get(INVULNERABLE_KEY, PersistentDataType.BOOLEAN)));
    }
}
