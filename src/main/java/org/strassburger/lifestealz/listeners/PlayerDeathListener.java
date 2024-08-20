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
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.worldguard.WorldGuardManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class PlayerDeathListener implements Listener {

    private final LifeStealZ plugin = LifeStealZ.getInstance();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (!plugin.getConfig().getStringList("worlds").contains(player.getWorld().getName())) return;

        // WorldGuard check
        if (plugin.hasWorldGuard() && !WorldGuardManager.checkHeartLossFlag(player)) return;

        final PlayerData playerData = plugin.getPlayerDataStorage().load(player.getUniqueId());

        final boolean isDeathByPlayer = killer != null && !killer.getUniqueId().equals(player.getUniqueId());

        // Natural death or death by player
        if ((!isDeathByPlayer && plugin.getConfig().getBoolean("looseHeartsToNature"))
                || (isDeathByPlayer && plugin.getConfig().getBoolean("looseHeartsToPlayer"))) {
            handleHeartLoss(event, player, killer, playerData, isDeathByPlayer);
        }
    }

    private void handleHeartLoss(PlayerDeathEvent event, Player player, Player killer, PlayerData playerData, boolean isDeathByPlayer) {
        final World world = player.getWorld();
        final double minHearts = plugin.getConfig().getInt("minHearts") * 2;

        // Check for elimination
        if (playerData.getMaxhp() - 2.0 <= minHearts) {
            handleElimination(event, player, killer, isDeathByPlayer);
            return;
        }

        // Drop hearts or handle heart gain for the killer (if applicable)
        if (plugin.getConfig().getBoolean("dropHeartsOnDeath")) {
            world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
        } else if (isDeathByPlayer) {
            handleKillerHeartGain(player, killer, world);
        }

        // Reduce the victim's hearts
        playerData.setMaxhp(playerData.getMaxhp() - 2.0);
        plugin.getPlayerDataStorage().save(playerData);
        LifeStealZ.setMaxHealth(player, playerData.getMaxhp());
    }

    private void handleElimination(PlayerDeathEvent event, Player player, Player killer, boolean isDeathByPlayer) {
        final List<String> elimCommands = plugin.getConfig().getStringList("eliminationCommands");
        final boolean disableBanOnElimination = plugin.getConfig().getBoolean("disablePlayerBanOnElimination");
        final boolean announceElimination = plugin.getConfig().getBoolean("announceElimination");
        final World world = player.getWorld();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (String command : elimCommands) {
                plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        command.replace("&player&", player.getName())
                );
            }
        }, 1L);

        if (disableBanOnElimination) {
            double respawnHP = plugin.getConfig().getInt("respawnHP") * 2;
            PlayerData playerData = plugin.getPlayerDataStorage().load(player.getUniqueId());
            playerData.setMaxhp(respawnHP);
            plugin.getPlayerDataStorage().save(playerData);
            LifeStealZ.setMaxHealth(player, respawnHP);
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Component kickMessage = MessageUtils.getAndFormatMsg(
                    false,
                    "messages.eliminatedjoin",
                    "&cYou don't have any hearts left!");
            player.kick(kickMessage);
        }, 1L);

        if (announceElimination) {
            String messageKey = isDeathByPlayer ? "messages.eliminationAnnouncement" : "messages.eliminateionAnnouncementNature";
            Bukkit.broadcast(MessageUtils.getAndFormatMsg(false, messageKey,
                    isDeathByPlayer ? "&c%player% &7has been eliminated by &c%killer%&7!" : "&c%player% &7has been eliminated!",
                    new MessageUtils.Replaceable("%player%", player.getName()),
                    new MessageUtils.Replaceable("%killer%", killer != null ? killer.getName() : "")));
            event.setDeathMessage(null);
        }

        // I suppose this is where webhook support should go here eventually.

        if (plugin.getConfig().getBoolean("dropHeartsOnDeath")) {
            world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
        }

        PlayerData playerData = plugin.getPlayerDataStorage().load(player.getUniqueId());
        playerData.setMaxhp(0.0);
        plugin.getPlayerDataStorage().save(playerData);
    }

    private void handleKillerHeartGain(Player player, Player killer, World world) {
        final boolean heartGainCooldownEnabled = plugin.getConfig().getBoolean("heartGainCooldown.enabled");
        final long heartGainCooldown = plugin.getConfig().getLong("heartGainCooldown.cooldown");
        final boolean heartGainCooldownDropOnCooldown = plugin.getConfig().getBoolean("heartGainCooldown.dropOnCooldown");
        final double maxHearts = plugin.getConfig().getInt("maxHearts") * 2;
        final double minHearts = plugin.getConfig().getInt("minHearts") * 2;
        final boolean heartRewardOnElimination = plugin.getConfig().getBoolean("heartRewardOnElimination");
        final boolean dropHeartsIfMax = plugin.getConfig().getBoolean("dropHeartsIfMax");

        PlayerData playerData = plugin.getPlayerDataStorage().load(player.getUniqueId());
        PlayerData killerPlayerData = plugin.getPlayerDataStorage().load(killer.getUniqueId());

        // Anti-alt logic
        if (handleAntiAltLogic(player, killer)) return;

        if (heartGainCooldownEnabled
                && CooldownManager.lastHeartGain.get(killer.getUniqueId()) != null
                && CooldownManager.lastHeartGain.get(killer.getUniqueId()) + heartGainCooldown > System.currentTimeMillis()) {
            killer.sendMessage(MessageUtils.getAndFormatMsg(false, "heartGainCooldown", "&cYou have to wait before gaining another heart!"));
            if (heartGainCooldownDropOnCooldown) {
                world.dropItemNaturally(player.getLocation(), CustomItemManager.createHeart());
            }
        } else if (playerData.getMaxhp() - 2.0 > minHearts || (playerData.getMaxhp() - 2.0 <= minHearts && heartRewardOnElimination)) {
            if (killerPlayerData.getMaxhp() + 2.0 > maxHearts) {
                if (dropHeartsIfMax) {
                    world.dropItemNaturally(killer.getLocation(), CustomItemManager.createHeart());
                } else {
                    killer.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.maxHeartLimitReached",
                            "&cYou already reached the limit of %limit% hearts!",
                            new MessageUtils.Replaceable("%limit%", (int) maxHearts / 2 + "")));
                }
            } else {
                killerPlayerData.setMaxhp(killerPlayerData.getMaxhp() + 2.0);
                plugin.getPlayerDataStorage().save(killerPlayerData);
                LifeStealZ.setMaxHealth(killer, killerPlayerData.getMaxhp());
                killer.setHealth(Math.min(killer.getHealth() + 2.0, killerPlayerData.getMaxhp()));
                CooldownManager.lastHeartGain.put(killer.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    private boolean handleAntiAltLogic(Player player, Player killer) {
        final String victimIP = getPlayerIP(player);
        final String killerIP = getPlayerIP(killer);

        if (victimIP != null && victimIP.equals(killerIP) && plugin.getConfig().getBoolean("antiAlt.enabled")) {
            if (plugin.getConfig().getBoolean("antiAlt.logAttempt")) {
                plugin.getLogger().info("[ALT WARNING] Player " + killer.getName() + " tried to kill "
                        + player.getName() + " with the same IP address! (Probably an alt account)");
            }
            if (plugin.getConfig().getBoolean("antiAlt.sendMessage")) {
                killer.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.altKill",
                        "&cPlease don't kill alts! This attempt has been logged!"));
            }
            for (String command : plugin.getConfig().getStringList("antiAlt.commands")) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        command.replace("&player&", killer.getName()));
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        command.replace("&player&", player.getName()));
            }
            return plugin.getConfig().getBoolean("antiAlt.preventKill");
        }
        return false;
    }

    private String getPlayerIP(Player player) {
        InetSocketAddress inetSocketAddress = player.getAddress();
        if (inetSocketAddress == null) return null;
        InetAddress address = inetSocketAddress.getAddress();
        return address.getHostAddress();
    }

}

