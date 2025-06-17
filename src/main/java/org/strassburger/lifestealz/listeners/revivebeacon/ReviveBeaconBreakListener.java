package org.strassburger.lifestealz.listeners.revivebeacon;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.ReviveTask;
import org.strassburger.lifestealz.util.customblocks.CustomBlock;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

public final class ReviveBeaconBreakListener implements Listener {
    private final LifeStealZ plugin;

    public ReviveBeaconBreakListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onReviveBeaconBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!CustomBlock.REVIVE_BEACON.is(block)) return;

        plugin.getReviveBeaconEffectManager().clearAllEffects(block.getLocation());
        Location location = block.getLocation();

        ReviveTask reviveTask = LifeStealZ.reviveTasks.remove(location);
        if (reviveTask != null) {
            if (!reviveTask.task().isCancelled()) reviveTask.task().cancel();
            location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
            Player reviver = Bukkit.getPlayer(reviveTask.reviver());
            if (reviver != null && reviver.isOnline()) {
                reviver.sendMessage(MessageUtils.getAndFormatMsg(
                        true,
                        "reviveBeaconBreak",
                        "&7Your revive beacon has been broken by &c%breaker%&7, the revive process has been cancelled.",
                        new MessageUtils.Replaceable("%breaker%", event.getPlayer().getName())
                ));
            }
        }

        event.setDropItems(false);
        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        String customID = CustomBlock.REVIVE_BEACON.getCustomItemId(block);
        if (customID == null) return;
        ItemStack item = CustomItemManager.createCustomItem(customID);
        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(CustomBlock.REVIVE_BEACON::is);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(CustomBlock.REVIVE_BEACON::is);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (CustomBlock.REVIVE_BEACON.is(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (CustomBlock.REVIVE_BEACON.is(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (CustomBlock.REVIVE_BEACON.is(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}
