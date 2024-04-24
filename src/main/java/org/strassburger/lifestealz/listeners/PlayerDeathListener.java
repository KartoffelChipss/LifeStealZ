package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.CustomItemManager;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.worldguard.WorldGuardManager;

import java.util.List;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        World world = player.getWorld();

        PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

        List<String> elimCommands = LifeStealZ.getInstance().getConfig().getStringList("eliminationCommands");
        boolean disableBanOnElimination = LifeStealZ.getInstance().getConfig().getBoolean("disablePlayerBanOnElimination");
        boolean announceElimination = LifeStealZ.getInstance().getConfig().getBoolean("announceElimination");
        boolean dropHeartsOnDeath = LifeStealZ.getInstance().getConfig().getBoolean("dropHearts");
        boolean dropHeartsIfMax = LifeStealZ.getInstance().getConfig().getBoolean("dropHeartsIfMax");
        double maxHearts = LifeStealZ.getInstance().getConfig().getInt("maxHearts") * 2;

        if (!LifeStealZ.getInstance().getConfig().getStringList("worlds").contains(player.getWorld().getName())) return;

        // if player is in a region where the heartloss flag is set to deny, return
        if (LifeStealZ.getInstance().hasWorldGuard()) {
            if (!WorldGuardManager.checkHeartLossFlag(player)) return;
        }

        // Player died a natural death (e.g. fall damage)
        if (killer == null && LifeStealZ.getInstance().getConfig().getBoolean("looseHeartsToNature")) {
            if (playerData.getMaxhp() - 2.0 <= 0.0) {
                for (String command : elimCommands) {
                    LifeStealZ.getInstance().getServer().dispatchCommand(LifeStealZ.getInstance().getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                }

                if (disableBanOnElimination) {
                    double respawnHP = LifeStealZ.getInstance().getConfig().getInt("respawnHP") * 2;
                    playerData.setMaxhp(respawnHP);
                    LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                    LifeStealZ.setMaxHealth(player, respawnHP);
                    return;
                }

                Component kickMessage = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
                player.kick(kickMessage);

                if (announceElimination) {
                    Bukkit.broadcast(MessageUtils.getAndFormatMsg(false, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new Replaceable("%player%", player.getName())));
                }

                playerData.setMaxhp(0.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
            } else {
                playerData.setMaxhp(playerData.getMaxhp() - 2.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, playerData.getMaxhp());
            }

            return;
        }

        // Player was killed by another player
        if (killer != null && LifeStealZ.getInstance().getConfig().getBoolean("looseHeartsToPlayer")) {
            PlayerData killerPlayerData = LifeStealZ.getInstance().getPlayerDataStorage().load(killer.getUniqueId());

            // Handle killer gaining hearts
            if (dropHeartsOnDeath) world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
            else {
                if (killerPlayerData.getMaxhp() + 2.0 > maxHearts) {
                    if (dropHeartsIfMax) world.dropItemNaturally(killer.getLocation(), CustomItemManager.createHeart());
                    else killer.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new Replaceable("%limit%", (int) maxHearts / 2 + "")));
                } else {
                    killerPlayerData.setMaxhp(killerPlayerData.getMaxhp() + 2.0);
                    LifeStealZ.getInstance().getPlayerDataStorage().save(killerPlayerData);
                    LifeStealZ.setMaxHealth(killer, killerPlayerData.getMaxhp());
                    killer.setHealth(Math.min(killer.getHealth() + 2.0, killerPlayerData.getMaxhp()));
                }
            }

            // Handle victim loosing hearts
            if (playerData.getMaxhp() - 2.0 <= 0.0) {
                for (String command : elimCommands) {
                    LifeStealZ.getInstance().getServer().dispatchCommand(LifeStealZ.getInstance().getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                }

                if (disableBanOnElimination) {
                    double respawnHP = LifeStealZ.getInstance().getConfig().getInt("respawnHP") * 2;
                    playerData.setMaxhp(respawnHP);
                    LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                    LifeStealZ.setMaxHealth(player, respawnHP);
                    return;
                }

                // Simulate the player dying before getting banned
                //boolean keepInventory = Boolean.TRUE.equals(world.getGameRuleValue(GameRule.KEEP_INVENTORY));
                //if (!keepInventory) {
                //    for (ItemStack item : player.getInventory().getContents()) if (item != null) player.getWorld().dropItemNaturally(player.getLocation(), item);
                //    player.getInventory().clear();
                //}

                Component kickMessage = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
                player.kick(kickMessage);

                if (announceElimination) {
                    Bukkit.broadcast(MessageUtils.getAndFormatMsg(false, "messages.eliminationAnnouncement", "&c%player% &7has been eliminated by &c%killer%&7!", new Replaceable("%player%", player.getName()), new Replaceable("%killer%", killer.getName())));
                }

                playerData.setMaxhp(0.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
            } else {
                playerData.setMaxhp(playerData.getMaxhp() - 2.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, playerData.getMaxhp());
            }
        }
    }
}
