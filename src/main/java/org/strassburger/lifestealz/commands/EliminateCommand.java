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
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class EliminateCommand implements CommandExecutor, TabCompleter {
    private LifeStealZ plugin;

    public EliminateCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }


    private final LifeStealZ plugin = LifeStealZ.getInstance();
    WhitelistManager wm = new WhitelistManager();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!wm.isWorldWhitelisted(sender)) return false;

        String targetPlayerName = (args != null && args.length > 0) ? args[0] : null;
        if (targetPlayerName == null) {
            throwUsageError(sender);
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            throwUsageError(sender);
            return false;
        }

        eliminatePlayer(sender, targetPlayer);
        return true;
    }


    private void eliminatePlayer(CommandSender sender, Player targetPlayer) {
        PlayerData playerData = plugin.getPlayerDataStorage().load(targetPlayer.getUniqueId());
        playerData.setMaxhp(0.0);
        plugin.getPlayerDataStorage().save(playerData);

        dropPlayerInventory(targetPlayer);

        Component kickmsg = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
        targetPlayer.kick(kickmsg, PlayerKickEvent.Cause.BANNED);

        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.eliminateSuc", "&7You successfully eliminated &c%player%&7!",
                new MessageUtils.Replaceable("%player%", targetPlayer.getName())));

        if (plugin.getConfig().getBoolean("announceElimination")) {
            Component elimAnnouncementMsg = MessageUtils.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!",
                    new MessageUtils.Replaceable("%player%", targetPlayer.getName()));
            Bukkit.broadcast(elimAnnouncementMsg);
        }
    }

    private void dropPlayerInventory(Player targetPlayer) {
        for (ItemStack item : targetPlayer.getInventory().getContents()) {
            if (item != null) targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), item);
        }
        targetPlayer.getInventory().clear();
    }

    private void throwUsageError(CommandSender sender) {
        Component usageMessage = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/eliminate <player>"));
        sender.sendMessage(usageMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}