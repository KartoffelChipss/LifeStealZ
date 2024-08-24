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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
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

        int withdrawHearts = 0;
        try {
            withdrawHearts = args != null && args.length > 0 ? Integer.parseInt(args[0]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new MessageUtils.Replaceable("%usage%", "/withdrawheart <amount> [confirm]")));
            return false;
        }

        String confirmOption = args != null && args.length > 1 ? args[1] : null;

        Player player = (Player) sender;
        PlayerData playerdata = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

        boolean withdrawtoDeath = LifeStealZ.getInstance().getConfig().getBoolean("allowDyingFromWithdraw");

        if (withdrawHearts < 1) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.withdrawMin", "&cYou can't withdraw less than 1 heart!"));
            return false;
        }

        // Check for available inventory space including existing heart stacks
        int heartsToWithdraw = withdrawHearts;
        int availableSpace = 0;
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (ItemStack item : contents) {
            if (item == null) {
                availableSpace += 64;
            } else if (CustomItemManager.isHeartItem(item)) {
                availableSpace += (64 - item.getAmount());
            }
            if (availableSpace >= heartsToWithdraw) {
                break;
            }
        }

        if (availableSpace < heartsToWithdraw) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noInventorySpace", "&cYou don't have enough inventory space to withdraw that many hearts!"));
            return false;
        }

        double resultingHealth = playerdata.getMaxhp() - ((double) withdrawHearts * 2);
        double minHealth = LifeStealZ.getInstance().getConfig().getDouble("minHearts", 2.0) * 2; // Default to 2.0 if not set

        if (resultingHealth < minHealth) {
            if (confirmOption == null || !confirmOption.equals("confirm")) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noWithdraw", "&cYou would be eliminated if you withdraw a heart!"));
                if (withdrawtoDeath)
                    sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.withdrawConfirmmsg", "&8&oUse <underlined><click:SUGGEST_COMMAND:/withdrawheart %amount% confirm>/withdrawheart %amount% confirm</click></underlined> if you really want to withdraw a heart", new MessageUtils.Replaceable("%amount%", withdrawHearts + "")));
                return false;
            }

            if (!withdrawtoDeath) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.noWithdraw", "&cYou would be eliminated if you withdraw a heart!"));
                return false;
            }

            for (int i = 0; i < playerdata.getMaxhp() / 2; i++) {
                player.getInventory().addItem(CustomItemManager.createHeart());
            }

            playerdata.setMaxhp(0.0);
            LifeStealZ.getInstance().getPlayerDataStorage().save(playerdata);

            Component kickmsg = MessageUtils.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!");
            player.kick(kickmsg, PlayerKickEvent.Cause.BANNED);

            if (LifeStealZ.getInstance().getConfig().getBoolean("announceElimination")) {
                Component elimAnnouncementMsg = MessageUtils.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", new MessageUtils.Replaceable("%player%", player.getName()));
                Bukkit.broadcast(elimAnnouncementMsg);
            }

            return false;
        }


        playerdata.setMaxhp(playerdata.getMaxhp() - (double) withdrawHearts * 2);
        LifeStealZ.getInstance().getPlayerDataStorage().save(playerdata);
        LifeStealZ.setMaxHealth(player, playerdata.getMaxhp());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 500.0f, 1.0f);

        for (int i = 0; i < withdrawHearts; i++) {
            player.getInventory().addItem(CustomItemManager.createHeart());
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        else return null;
    }
}
