package org.strassburger.lifestealz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class ReviveCommand implements CommandExecutor, TabCompleter {
    private final LifeStealZ plugin;

    public ReviveCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    private static final String BYPASS_OPTION = "bypass";
    WhitelistManager wm = new WhitelistManager();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!wm.isWorldWhitelisted(sender)) return false;

        String targetPlayerName = args.length > 0 ? args[0] : null;
        String bypassOption = args.length > 1 ? args[1] : null;

        if (targetPlayerName == null) {
            throwUsageError(sender);
            return false;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
        PlayerData playerData = plugin.getStorage().load(targetPlayer.getUniqueId());

        if (playerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noPlayerData", "&cThis player has not played on this server yet!"));
            return false;
        }

        // Check if the player has reached the revive limit or has the bypass permission
        if (!canRevive(sender, playerData, bypassOption)) {
            return false;
        }

        // Check if the player is eliminated
        if (!isEligibleForRevive(sender, playerData)) {
            return false;
        }

        revivePlayer(sender, targetPlayerName, playerData);

        return false;
    }

    /**
     * Checks if the player can be revived
     * @param sender The command sender
     * @param playerData The player data of the player to be revived
     * @param bypassOption The bypass option
     * @return True if the player can be revived, false otherwise
     */
    private boolean canRevive(CommandSender sender, PlayerData playerData, String bypassOption) {
        int maxRevives = plugin.getConfig().getInt("maxRevives");
        boolean hasBypassPermission = sender.hasPermission("lifestealz.bypassrevivelimit");

        if (maxRevives != -1 && playerData.getHasbeenRevived() >= maxRevives &&
                (!BYPASS_OPTION.equals(bypassOption) || !hasBypassPermission)) {

            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.reviveMaxReached",
                    "&cThis player has already been revived %amount% times!",
                    new MessageUtils.Replaceable("%amount%", Integer.toString(playerData.getHasbeenRevived()))));
            return false;
        }
        return true;
    }

    /**
     * Checks if the player is eligible for a revive (e.g. has been eliminated)
     * @param sender The command sender
     * @param playerData The player data of the player to be revived
     * @return True if the player is eligible for a revive, false otherwise
     */
    private boolean isEligibleForRevive(CommandSender sender, PlayerData playerData) {
        int minHearts = plugin.getConfig().getInt("minHearts");

        if (playerData.getMaxhp() > minHearts * 2) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!"));
            return false;
        }
        return true;
    }

    /**
     * Revives the player
     * @param sender The command sender
     * @param targetPlayerName The name of the player to be revived
     * @param playerData The player data of the player to be revived
     */
    private void revivePlayer(CommandSender sender, String targetPlayerName, PlayerData playerData) {
        playerData.setMaxhp(plugin.getConfig().getDouble("respawnHearts") * 2);
        playerData.setHasbeenRevived(playerData.getHasbeenRevived() + 1);
        plugin.getStorage().save(playerData);

        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveSuccess",
                "&7You successfully revived &c%player%&7!",
                new MessageUtils.Replaceable("%player%", targetPlayerName)));
    }

    /**
     * Throws a usage error message
     * @param sender The command sender
     */
    private void throwUsageError(CommandSender sender) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "messages.usageError",
                "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/revive <player>"));
        sender.sendMessage(usageMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2 && sender.hasPermission("lifestealz.bypassrevivelimit")) {
            return List.of(BYPASS_OPTION);
        }
        return null;
    }
}