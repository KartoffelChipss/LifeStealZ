package org.strassburger.lifestealz.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

public class InteractionEntityEventListener implements Listener {
    private final LifeStealZ plugin;

    public InteractionEntityEventListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractionEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) item = event.getPlayer().getInventory().getItemInOffHand();
        if (item.getType() == Material.AIR) return;

        boolean preventItemFrames = plugin.getConfig().getBoolean("preventCustomItemsInItemFrames");

        if (preventItemFrames && (CustomItemManager.isHeartItem(item) || CustomItemManager.isReviveItem(item))) {
            event.setCancelled(true);
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.itemFramesDisabled", "&cYou cannot put custom items in itemframes!"));
        }
    }
}
