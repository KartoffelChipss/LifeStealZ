package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;

import java.util.List;

import static com.zetaplugins.lifestealz.util.commands.CommandUtils.*;

public final class GracePeriodSubcommand implements SubCommand {
    private final LifeStealZ plugin;

    public GracePeriodSubcommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }

        if (args.length < 3) {
            throwUsageError(sender, getUsage());
            return false;
        }

        List<OfflinePlayer> targetPlayers = parseOfflinePlayer(args[1], true, true, plugin);

        if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
        }

        String secondArg = args[2].toLowerCase();

        switch (secondArg) {
            case "skip":
                handleGracePeriod(sender, targetPlayers, GracePeriodAction.SKIP);
                break;
            case "reset":
                handleGracePeriod(sender, targetPlayers, GracePeriodAction.RESET);
                break;
            default:
                throwUsageError(sender, getUsage());
                return false;
        }

        return true;
    }

    private void handleGracePeriod(CommandSender sender, List<OfflinePlayer> targetPlayers, GracePeriodAction gracePeriodAction) {
        int successCount = 0;
        OfflinePlayer lastSucessfulPlayer = null;

        for (OfflinePlayer targetPlayer : targetPlayers) {
            boolean success = gracePeriodAction == GracePeriodAction.SKIP ?
                    plugin.getGracePeriodManager().skipGracePeriod(targetPlayer) :
                    plugin.getGracePeriodManager().resetGracePeriod(targetPlayer);

            if (success) {
                successCount++;
                lastSucessfulPlayer = targetPlayer;
            }
        }

        if (successCount == 0 || successCount > 1) {
            sender.sendMessage(
                    gracePeriodAction == GracePeriodAction.SKIP ?
                            MessageUtils.getAndFormatMsg(
                                    true,
                                    "gracePeriodSkipSuccess",
                                    "&7Successfully skipped the grace period for &c%playerCount% &7players.",
                                    new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount))
                            ) : MessageUtils.getAndFormatMsg(
                            true,
                                "gracePeriodResetSuccess",
                                "&7Successfully reset the grace period for &c%playerCount% &7players.",
                                new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount))
                            )
            );
        } else {
            sender.sendMessage(
                    gracePeriodAction == GracePeriodAction.SKIP ?
                            MessageUtils.getAndFormatMsg(
                                    true,
                                    "gracePeriodSkipSuccessOnePlayer",
                                    "&7Successfully skipped the grace period for &c%player%&7.",
                                    new MessageUtils.Replaceable("%player%", lastSucessfulPlayer.getName())
                            ) : MessageUtils.getAndFormatMsg(
                            true,
                                "gracePeriodResetSuccessOnePlayer",
                                "&7Successfully reset the grace period for &c%player%&7.",
                                new MessageUtils.Replaceable("%player%", lastSucessfulPlayer.getName())
                            )
            );
        }
    }

    @Override
    public String getUsage() {
        return "/lifestealz graceperiod <player> <skip | reset>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.graceperiod");
    }

    private enum GracePeriodAction {
        SKIP,
        RESET
    }
}