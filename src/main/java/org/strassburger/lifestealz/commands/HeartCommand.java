package org.strassburger.lifestealz.commands;

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
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class HeartCommand implements CommandExecutor, TabCompleter {
    private final LifeStealZ plugin;

    public HeartCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    WhitelistManager wm = new WhitelistManager();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!wm.isWorldWhitelisted(sender)) return false;

        String targetName = (args != null && args.length > 0) ? args[0] : null;

        if (targetName == null) {
            return handleSelfHeartCheck(sender);
        } else {
            return handleOtherHeartCheck(sender, targetName);
        }
    }

    /**
     * Handles the heart check for the player who executed the command
     * @param sender The command sender
     * @return Whether the command was executed successfully
     */
    private boolean handleSelfHeartCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.specifyPlayerOrBePlayer", "&cYou need to either specify a player or be a player yourself!"));
            return false;
        }

        Player player = (Player) sender;
        PlayerData playerdata = plugin.getStorage().load(player.getUniqueId());
        int heartCount = (int) Math.floor(playerdata.getMaxHealth() / 2);
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.viewheartsYou", "&7You have &c%amount% &7hearts!",
                new MessageUtils.Replaceable("%amount%", Integer.toString(heartCount))));
        return true;
    }

    /**
     * Handles the heart check for another player
     * @param sender The command sender
     * @param targetName The name of the player to check
     * @return Whether the command was executed successfully
     */
    private boolean handleOtherHeartCheck(CommandSender sender, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (target.getName() == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
            return false;
        }

        PlayerData playerdata = plugin.getStorage().load(target.getUniqueId());

        if (playerdata == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
            return false;
        }

        int heartCount = (int) Math.floor(playerdata.getMaxHealth() / 2);
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.viewheartsOther", "&c%player% &7currently has &c%amount% &7hearts!",
                new MessageUtils.Replaceable("%amount%", Integer.toString(heartCount)),
                new MessageUtils.Replaceable("%player%", target.getName())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}