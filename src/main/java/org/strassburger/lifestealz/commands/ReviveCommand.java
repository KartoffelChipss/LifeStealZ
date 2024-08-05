package org.strassburger.lifestealz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviveCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> worldWhitelist = LifeStealZ.getInstance().getConfig().getStringList("worlds");
        if (sender instanceof Player && !worldWhitelist.contains(((Player) sender).getLocation().getWorld().getName())) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
            return false;
        }

        String targetPlayerName = args != null && args.length > 0 ? args[0] : null;
        String bypassOption = args != null && args.length > 1 ? args[1] : null;

        if (targetPlayerName == null) {
            throwUsageError(sender);
            return false;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(targetPlayer.getUniqueId());

        if (playerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noPlayerData", "&cThis player has not played on this server yet!"));
            return false;
        }

        int maxRevives = LifeStealZ.getInstance().getConfig().getInt("maxRevives");

        if (maxRevives != -1 && playerData.getHasbeenRevived() >= maxRevives && (bypassOption == null || !bypassOption.equals("bypass") || !sender.hasPermission("lifestealz.bypassrevivelimit"))) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.reviveMaxReached", "&cThis player has already been revived %amount% times!", new Replaceable("%amount%", Integer.toString(playerData.getHasbeenRevived()))));
            return false;
        }

        int minHearts = LifeStealZ.getInstance().getConfig().getInt("minHearts");

        if (playerData.getMaxhp() > minHearts * 2) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!"));
            return false;
        }

        playerData.setMaxhp(LifeStealZ.getInstance().getConfig().getDouble("respawnHP") * 2);
        playerData.setHasbeenRevived(playerData.getHasbeenRevived() + 1);
        LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);

        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reviveSuccess", "&7You successfully revived &c%player%&7!", new Replaceable("%player%", targetPlayerName)));

        return false;
    }

    public void throwUsageError(CommandSender sender) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", "/revive <player>"));
        sender.sendMessage(usageMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2 && sender.hasPermission("lifestealz.bypassrevivelimit")) return List.of("bypass");
        return null;
    }
}
