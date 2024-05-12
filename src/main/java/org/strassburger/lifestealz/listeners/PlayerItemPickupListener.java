package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.CooldownManager;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

public class PlayerItemPickupListener implements Listener {
    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();

        boolean heartGainCooldownEnabled = LifeStealZ.getInstance().getConfig().getBoolean("heartGainCooldown.enabled");
        long heartGainCooldown = LifeStealZ.getInstance().getConfig().getLong("heartGainCooldown.cooldown");
        boolean heartGainCooldownPreventPickup = LifeStealZ.getInstance().getConfig().getBoolean("heartGainCooldown.preventPickup");

        if (!CustomItemManager.isHeartItem(itemStack)) return;

        if (heartGainCooldownEnabled && heartGainCooldownPreventPickup && CooldownManager.lastHeartGain.get(player.getUniqueId()) != null && CooldownManager.lastHeartGain.get(player.getUniqueId()) + heartGainCooldown > System.currentTimeMillis()) {
            event.setCancelled(true);
            if (CooldownManager.lastHeartPickupMessage.get(player.getUniqueId()) == null || CooldownManager.lastHeartPickupMessage.get(player.getUniqueId()) + 1000 < System.currentTimeMillis()) {
                player.sendMessage(MessageUtils.getAndFormatMsg(false, "heartGainCooldown", "&cYou have to wait before gaining another heart!"));
                CooldownManager.lastHeartPickupMessage.put(player.getUniqueId(), System.currentTimeMillis());
            }
            return;
        }

        CooldownManager.lastHeartGain.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
