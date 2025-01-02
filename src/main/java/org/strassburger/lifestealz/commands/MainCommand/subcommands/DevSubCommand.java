package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.GracePeriodManager;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;
import org.strassburger.lifestealz.util.customitems.CustomItem;
import org.strassburger.lifestealz.util.storage.PlayerData;

import static org.strassburger.lifestealz.util.commands.CommandUtils.throwUsageError;

public class DevSubCommand implements SubCommand {
    private final LifeStealZ plugin;

    public DevSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }

        if (args.length < 2) {
            throwUsageError(sender, getUsage());
            return false;
        }

        String optionTwo = args[1];

        if (optionTwo.equals("giveForbiddenitem")) {
            if (!(sender instanceof Player)) return false;

            Player player = (Player) sender;

            player.getInventory().addItem(new CustomItem(Material.BARRIER).makeForbidden().getItemStack());

            return true;
        }

        if (optionTwo.equals("isInGracePeriod")) {
            if (args.length < 3 && !(sender instanceof Player)) {
                throwUsageError(sender, "/lifestealz dev isInGracePeriod [player]");
                return false;
            }

            Player player = args.length > 2 ? plugin.getServer().getPlayer(args[2]) : (Player) sender;
            if (player == null) {
                throwUsageError(sender, "/lifestealz dev isInGracePeriod [player]");
                return false;
            }

            GracePeriodManager gracePeriodManager = plugin.getGracePeriodManager();

            String gracePeriodColor = gracePeriodManager.isInGracePeriod(player) ? "&a" : "&c";
            String gracePeriodEnabledColor = gracePeriodManager.isEnabled() ? "&a" : "&c";

            sender.sendMessage(MessageUtils.formatMsg(
                    "&7Is &c" + player.getName() + " &7in grace period? "
                            + gracePeriodColor + gracePeriodManager.isInGracePeriod(player)
                            + (gracePeriodManager.isInGracePeriod(player) ? " &7(" + gracePeriodManager.getGracePeriodRemaining(player).orElse(-1) + "remaining)" : "")
                            + "\n&7Grace period enabled: " + gracePeriodEnabledColor + gracePeriodManager.isEnabled() + "&7"
            ));
        }

        if (optionTwo.equals("setFirstJoinDate")) {
            if (args.length < 3 && !(sender instanceof Player)) {
                throwUsageError(sender, "/lifestealz dev setFirstJoinDate [player]");
                return false;
            }

            Player player = args.length > 2 ? plugin.getServer().getPlayer(args[2]) : (Player) sender;
            if (player == null) {
                throwUsageError(sender, "/lifestealz dev setFirstJoinDate [player]");
                return false;
            }

            final long newFirstJoin = System.currentTimeMillis();

            PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
            playerData.setFirstJoin(newFirstJoin);
            plugin.getStorage().save(playerData);
            plugin.getGracePeriodManager().startGracePeriod(player);
        }

        if (optionTwo.equals("refreshCaches")) {
            plugin.getEliminatedPlayersCache().reloadCache();
            sender.sendMessage(MessageUtils.formatMsg("&7Caches reloaded!"));
        }

        return false;
    }

    @Override
    public String getUsage() {
        return "/lifestealz dev <giveForbiddenitem | isInGracePeriod | setFirstJoinDate | refreshCaches>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isOp();
    }
}
