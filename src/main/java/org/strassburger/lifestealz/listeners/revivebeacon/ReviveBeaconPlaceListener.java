package org.strassburger.lifestealz.listeners.revivebeacon;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.customblocks.CustomBlock;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
import org.strassburger.lifestealz.util.customitems.CustomItemType;

public final class ReviveBeaconPlaceListener implements Listener {
    private final LifeStealZ plugin;

    public ReviveBeaconPlaceListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.BEACON)) return;

        ItemStack itemInHand = event.getItemInHand();
        if (!CustomItemType.REVIVE_BEACON.is(itemInHand)) return;

        String customItemId = CustomItemManager.getCustomItemId(itemInHand);

        CustomBlock.REVIVE_BEACON.make(block, customItemId);

        plugin.getReviveBeaconEffectManager().startIdleEffects(block.getLocation());
    }
}
