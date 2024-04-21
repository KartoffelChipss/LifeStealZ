package org.strassburger.lifestealz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.CustomItemManager;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class WithdrawCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> worldWhitelist = LifeStealZ.getInstance().getConfig().getStringList("worlds");
        if (sender instanceof Player && !worldWhitelist.contains(((Player) sender).getLocation().getWorld().getName())) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
            return false;
        }

        if (!(sender instanceof Player)) return false;

        String confirmOption = args != null && args.length > 0 ? args[0] : null;

        Player player = (Player) sender;
        PlayerData playerdata = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

        boolean withdrawtoDeath = LifeStealZ.getInstance().getConfig().getBoolean("allowDyingFromWithdraw");

        if (playerdata.getMaxhp() <= 2.0) {
            if (confirmOption == null || !confirmOption.equals("confirm")) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noWithdraw", "&cYou would be eliminated, if you withdraw a heart!"));
                if (withdrawtoDeath) sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.withdrawConfirmmsg", "&8&oUse /withdrawheart confirm if you really want to withdraw a heart"));
                return false;
            }

            if (!withdrawtoDeath) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noWithdraw", "&cYou would be eliminated, if you withdraw a heart!"));
                return false;
            }

            player.getInventory().addItem(CustomItemManager.createHeart());

            playerdata.setMaxhp(0.0);
            LifeStealZ.getInstance().getPlayerDataStorage().save(playerdata);

            Component kickmsg = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
            player.kick(kickmsg, PlayerKickEvent.Cause.BANNED);

            if (LifeStealZ.getInstance().getConfig().getBoolean("announceElimination")) {
                Component elimAnnouncementMsg = MessageUtils.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new Replaceable("%player%", player.getName()));
                Bukkit.broadcast(elimAnnouncementMsg);
            }

            return false;
        }

        playerdata.setMaxhp(playerdata.getMaxhp() - 2.0);
        LifeStealZ.getInstance().getPlayerDataStorage().save(playerdata);
        LifeStealZ.setMaxHealth(player, playerdata.getMaxhp());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 500.0f, 1.0f);

        player.getInventory().addItem(CustomItemManager.createHeart());

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}
