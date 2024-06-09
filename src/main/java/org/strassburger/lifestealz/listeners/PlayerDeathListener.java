package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.CooldownManager;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.worldguard.WorldGuardManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        final World world = player.getWorld();

        PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

        List<String> elimCommands = LifeStealZ.getInstance().getConfig().getStringList("eliminationCommands");
        boolean heartRewardOnElimination = LifeStealZ.getInstance().getConfig().getBoolean("heartRewardOnElimination");
        boolean disableBanOnElimination = LifeStealZ.getInstance().getConfig().getBoolean("disablePlayerBanOnElimination");
        boolean announceElimination = LifeStealZ.getInstance().getConfig().getBoolean("announceElimination");
        boolean dropHeartsOnDeath = LifeStealZ.getInstance().getConfig().getBoolean("dropHearts");
        boolean dropHeartsIfMax = LifeStealZ.getInstance().getConfig().getBoolean("dropHeartsIfMax");
        double maxHearts = LifeStealZ.getInstance().getConfig().getInt("maxHearts") * 2;
        double minHearts = LifeStealZ.getInstance().getConfig().getInt("minHearts") * 2;

        boolean heartGainCooldownEnabled = LifeStealZ.getInstance().getConfig().getBoolean("heartGainCooldown.enabled");
        long heartGainCooldown = LifeStealZ.getInstance().getConfig().getLong("heartGainCooldown.cooldown");
        boolean heartGainCooldowndropOnCooldown = LifeStealZ.getInstance().getConfig().getBoolean("heartGainCooldown.dropOnCooldown");

        if (!LifeStealZ.getInstance().getConfig().getStringList("worlds").contains(player.getWorld().getName())) return;

        // if player is in a region where the heartloss flag is set to deny, return
        if (LifeStealZ.getInstance().hasWorldGuard()) {
            if (!WorldGuardManager.checkHeartLossFlag(player)) return;
        }

        boolean isDeathByPlayer = killer != null && !killer.getUniqueId().equals(player.getUniqueId());

        // Player died a natural death (e.g. fall damage)
        if (!isDeathByPlayer && LifeStealZ.getInstance().getConfig().getBoolean("looseHeartsToNature")) {
            if (playerData.getMaxhp() - 2.0 <= minHearts) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(LifeStealZ.getInstance(), () -> {
                    for (String command : elimCommands) {
                        LifeStealZ.getInstance().getServer().dispatchCommand(LifeStealZ.getInstance().getServer().getConsoleSender(), command.replace("&player&", player.getName()));
                    }
                }, 1L);

                if (disableBanOnElimination) {
                    double respawnHP = LifeStealZ.getInstance().getConfig().getInt("respawnHP") * 2;
                    playerData.setMaxhp(respawnHP);
                    LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                    LifeStealZ.setMaxHealth(player, respawnHP);
                    return;
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(LifeStealZ.getInstance(), () -> {
                    Component kickMessage = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
                    player.kick(kickMessage);
                }, 1L);

                if (announceElimination) {
                    Bukkit.broadcast(MessageUtils.getAndFormatMsg(false, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new Replaceable("%player%", player.getName())));
                }

                if (dropHeartsOnDeath) world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());

                playerData.setMaxhp(0.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
            } else {
                if (dropHeartsOnDeath) world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
                playerData.setMaxhp(playerData.getMaxhp() - 2.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, playerData.getMaxhp());
            }

            return;
        }

        // Player was killed by another player
        if (isDeathByPlayer && LifeStealZ.getInstance().getConfig().getBoolean("looseHeartsToPlayer")) {
            PlayerData killerPlayerData = LifeStealZ.getInstance().getPlayerDataStorage().load(killer.getUniqueId());

            String victimIP = getPlayerIP(player);
            String killerIP = getPlayerIP(killer);

            // Anti alt logic (If killer and victim are on same IP)
            if (victimIP != null && victimIP.equals(killerIP) && LifeStealZ.getInstance().getConfig().getBoolean("antiAlt.enabled")) {
                if (LifeStealZ.getInstance().getConfig().getBoolean("antiAlt.logAttempt")) LifeStealZ.getInstance().getLogger().info("[ALT WARNING] Player " + killer.getName() + " tried to kill " + player.getName() + " with the same IP address! (Proably an alt account)");
                if (LifeStealZ.getInstance().getConfig().getBoolean("antiAlt.sendMessage")) {
                    killer.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.altKill", "&cPlease don't kill alts! This attempt has been logged!"));
                }
                for (String command : LifeStealZ.getInstance().getConfig().getStringList("antiAlt.commands")) LifeStealZ.getInstance().getServer().dispatchCommand(LifeStealZ.getInstance().getServer().getConsoleSender(), command.replace("&player&", killer.getName()));
                for (String command : LifeStealZ.getInstance().getConfig().getStringList("antiAlt.commands")) LifeStealZ.getInstance().getServer().dispatchCommand(LifeStealZ.getInstance().getServer().getConsoleSender(), command.replace("&player&", player.getName()));
                if (LifeStealZ.getInstance().getConfig().getBoolean("antiAlt.preventKill")) return;
            }

            if (heartGainCooldownEnabled && CooldownManager.lastHeartGain.get(killer.getUniqueId()) != null && CooldownManager.lastHeartGain.get(killer.getUniqueId()) + heartGainCooldown > System.currentTimeMillis()) {
                // Heart Gain is on cooldown
                killer.sendMessage(MessageUtils.getAndFormatMsg(false, "heartGainCooldown", "&cYou have to wait before gaining another heart!"));
                if (heartGainCooldowndropOnCooldown) world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
            } else {
                // Handle killer gaining hearts
                if (dropHeartsOnDeath) world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
                else {
                    if (playerData.getMaxhp() - 2.0 > minHearts || playerData.getMaxhp() - 2.0 <= minHearts && heartRewardOnElimination) {
                        if (killerPlayerData.getMaxhp() + 2.0 > maxHearts) {
                            if (dropHeartsIfMax) world.dropItemNaturally(killer.getLocation(), CustomItemManager.createHeart());
                            else killer.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new Replaceable("%limit%", (int) maxHearts / 2 + "")));
                        } else {
                            killerPlayerData.setMaxhp(killerPlayerData.getMaxhp() + 2.0);
                            LifeStealZ.getInstance().getPlayerDataStorage().save(killerPlayerData);
                            LifeStealZ.setMaxHealth(killer, killerPlayerData.getMaxhp());
                            killer.setHealth(Math.min(killer.getHealth() + 2.0, killerPlayerData.getMaxhp()));
                            CooldownManager.lastHeartGain.put(killer.getUniqueId(), System.currentTimeMillis());
                        }
                    }
                }
            }

            // Handle victim loosing hearts
            if (playerData.getMaxhp() - 2.0 <= minHearts) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(LifeStealZ.getInstance(), () -> {
                    for (String command : elimCommands) {
                        LifeStealZ.getInstance().getServer().dispatchCommand(LifeStealZ.getInstance().getServer().getConsoleSender(), command.replace("&player&", player.getName()));
                    }
                }, 1L);

                if (disableBanOnElimination) {
                    double respawnHP = LifeStealZ.getInstance().getConfig().getInt("respawnHP") * 2;
                    playerData.setMaxhp(respawnHP);
                    LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                    LifeStealZ.setMaxHealth(player, respawnHP);
                    return;
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(LifeStealZ.getInstance(), () -> {
                    Component kickMessage = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
                    player.kick(kickMessage);
                }, 1L);

                if (announceElimination) {
                    Bukkit.broadcast(MessageUtils.getAndFormatMsg(false, "messages.eliminationAnnouncement", "&c%player% &7has been eliminated by &c%killer%&7!", new Replaceable("%player%", player.getName()), new Replaceable("%killer%", killer.getName())));
                }

                playerData.setMaxhp(minHearts);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
            } else {
                playerData.setMaxhp(playerData.getMaxhp() - 2.0);
                LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);
                LifeStealZ.setMaxHealth(player, playerData.getMaxhp());
            }
        }
    }

    private String getPlayerIP(Player player) {
        InetSocketAddress inetSocketAddress = player.getAddress();
        if (inetSocketAddress == null) return null;
        InetAddress address = inetSocketAddress.getAddress();
        return address.getHostAddress();
    }
}
