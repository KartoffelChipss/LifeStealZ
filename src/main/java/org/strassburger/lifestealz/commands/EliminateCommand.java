package org.strassburger.lifestealz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class EliminateCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> worldWhitelist = LifeStealZ.getInstance().getConfig().getStringList("worlds");
        if (sender instanceof Player && !worldWhitelist.contains(((Player) sender).getLocation().getWorld().getName())) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
            return false;
        }

        String targetPlayerName = args != null && args.length > 0 ? args[0] : null;

        if (targetPlayerName == null) {
            throwUsageError(sender);
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            throwUsageError(sender);
            return false;
        }

        PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(targetPlayer.getUniqueId());
        playerData.setMaxhp(0.0);
        LifeStealZ.getInstance().getPlayerDataStorage().save(playerData);

        for (ItemStack item : targetPlayer.getInventory().getContents()) {
            if (item != null) targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), item);
        }

        targetPlayer.getInventory().clear();
        Component kickmsg = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
        targetPlayer.kick(kickmsg, PlayerKickEvent.Cause.BANNED);

        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.eliminateSuc", "&7You successfully eliminated &c%player%&7!", new Replaceable("%player%", targetPlayer.getName())));

        if (LifeStealZ.getInstance().getConfig().getBoolean("announceElimination")) {
            Component elimAannouncementMsg = MessageUtils.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new Replaceable("%player%", targetPlayer.getName()));
            Bukkit.broadcast(elimAannouncementMsg);
        }
        return false;
    }

    private void throwUsageError(CommandSender sender) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", "/eliminate <player>"));
        sender.sendMessage(usageMessage);
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}
