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
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class HeartCommand implements CommandExecutor, TabCompleter {
    private LifeStealZ plugin;

    public HeartCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> worldWhitelist = plugin.getConfig().getStringList("worlds");
        if (sender instanceof Player && !worldWhitelist.contains(((Player) sender).getLocation().getWorld().getName())) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
            return false;
        }

        String targetName = args != null && args.length > 0 ? args[0] : null;

        if (targetName == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.specifyPlayerOrBePlayer", "&cYou need to either specify a player or be a player yourself!"));
                return false;
            }
            Player player = (Player) sender;
            PlayerData playerdata = plugin.getPlayerDataStorage().load(player.getUniqueId());
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.viewheartsYou", "&7You have &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString((int) Math.floor(playerdata.getMaxhp() / 2)))));
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (target.getName() == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
            return false;
        }

        PlayerData playerdata = plugin.getPlayerDataStorage().load(target.getUniqueId());

        if (playerdata == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
            return false;
        }

        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.viewheartsOther", "&c%player% &7currently has &c%amount% &7hearts!", new MessageUtils.Replaceable("%amount%", Integer.toString((int) Math.floor(playerdata.getMaxhp() / 2))), new MessageUtils.Replaceable("%player%", target.getName())));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}
