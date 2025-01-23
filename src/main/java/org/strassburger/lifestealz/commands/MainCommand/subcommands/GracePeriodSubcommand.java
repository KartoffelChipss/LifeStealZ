package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;

import java.util.List;

import static org.strassburger.lifestealz.util.commands.CommandUtils.parsePlayerName;
import static org.strassburger.lifestealz.util.commands.CommandUtils.throwUsageError;

public class GracePeriodSubcommand implements SubCommand {
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

        List<Player> targetPlayers = parsePlayerName(args[1], true, plugin);

        if (targetPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
            return false;
        }

        String secondArg = args[2].toLowerCase();

        int successCount = 0;

        switch (secondArg) {
            case "skip": {
                for (Player targetPlayer : targetPlayers) {
                    boolean success = plugin.getGracePeriodManager().skipGracePeriod(targetPlayer);
                    if (success) successCount++;
                }
                break;
            }
            case "reset":
                for (Player targetPlayer : targetPlayers) {
                    boolean success = plugin.getGracePeriodManager().resetGracePeriod(targetPlayer);
                    if (success) successCount++;
                }
                break;
            default:
                throwUsageError(sender, getUsage());
                return false;
        }

        sender.sendMessage(
                MessageUtils.getAndFormatMsg(
                        true,
                        "gracePeriodSkipSuccess",
                        "&7Successfully skipped the grace period for &c%playerCount% &7players.",
                        new MessageUtils.Replaceable("%playerCount%", String.valueOf(successCount))
                )
        );

        return true;
    }

    @Override
    public String getUsage() {
        return "/lifestealz graceperiod <player> <skip | reset>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.graceperiod");
    }
}
