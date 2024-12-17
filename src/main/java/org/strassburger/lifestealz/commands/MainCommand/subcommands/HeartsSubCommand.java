package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

import static org.strassburger.lifestealz.util.commands.CommandUtils.parsePlayerName;
import static org.strassburger.lifestealz.util.commands.CommandUtils.throwUsageError;

public class HeartsSubCommand implements SubCommand {
    private final LifeStealZ plugin;
    private final FileConfiguration config;
    private final Storage storage;

    public HeartsSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.storage = plugin.getStorage();
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

        String optionTwo = args[1];
        List<String> possibleOptionTwo = List.of("add", "set", "remove", "get");

        if (optionTwo == null || !possibleOptionTwo.contains(optionTwo)) {
            throwUsageError(sender, getUsage());
            return false;
        }

        if (optionTwo.equals("get")) {
            Player player = parsePlayerName(args[2], false, plugin).get(0);

            if (player == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
                return false;
            }

            PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

            int hearts = (int) (playerData.getMaxHealth() / 2);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.getHearts", "&c%player% &7currently has &c%amount% &7hearts!",
                    new MessageUtils.Replaceable("%player%", player.getName()), new MessageUtils.Replaceable("%amount%", hearts + "")));
            return true;
        }

        if (args.length < 4) {
            throwUsageError(sender, getUsage());
            return false;
        }

        int amount = Integer.parseInt(args[3]);

        if (amount < 0) {
            throwUsageError(sender, getUsage());
            return false;
        }

        int finalAmount = amount;

        List<Player> targetPlayers = parsePlayerName(args[2], true, plugin);

        for (Player targetPlayer : targetPlayers) {
            if (targetPlayer == null && targetPlayers.size() == 1) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
                return false;
            }

            if (targetPlayer == null && targetPlayers.size() > 1) continue;

            assert targetPlayer != null;
            PlayerData targetPlayerData = plugin.getStorage().load(targetPlayer.getUniqueId());

            switch (optionTwo) {
                case "add": {
                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && targetPlayerData.getMaxHealth() + (amount * 2) > config.getInt("maxHearts") * 2) {
                        Component maxHeartsMsg = MessageUtils.getAndFormatMsg(true, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!",
                                new MessageUtils.Replaceable("%limit%", config.getInt("maxHearts") + ""));
                        sender.sendMessage(maxHeartsMsg);
                        return false;
                    }

                    targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() + (amount * 2));
                    storage.save(targetPlayerData);
                    LifeStealZ.setMaxHealth(targetPlayer, targetPlayerData.getMaxHealth());
                    finalAmount = (int) (targetPlayerData.getMaxHealth() / 2);
                    break;
                }
                case "set": {
                    if (amount == 0) {
                        sender.sendMessage(Component.text("§cYou cannot set the lives below or to zero"));
                        return false;
                    }

                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && amount > config.getInt("maxHearts")) {
                        Component maxHeartsMsg = MessageUtils.getAndFormatMsg(true, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!",
                                new MessageUtils.Replaceable("%limit%", config.getInt("maxHearts") + ""));
                        sender.sendMessage(maxHeartsMsg);
                        return false;
                    }

                    targetPlayerData.setMaxHealth(amount * 2);
                    storage.save(targetPlayerData);
                    LifeStealZ.setMaxHealth(targetPlayer, targetPlayerData.getMaxHealth());
                    break;
                }
                case "remove": {
                    if ((targetPlayerData.getMaxHealth() / 2) - (double) amount <= 0) {
                        sender.sendMessage(Component.text("§cYou cannot set the lives below or to zero"));
                        return false;
                    }

                    targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() - (amount * 2));
                    storage.save(targetPlayerData);
                    LifeStealZ.setMaxHealth(targetPlayer, targetPlayerData.getMaxHealth());
                    finalAmount = (int) (targetPlayerData.getMaxHealth() / 2);
                    break;
                }
            }
        }

        String concatenatedPlayerNames = targetPlayers.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));

        Component setHeartsConfirmMessage = MessageUtils.getAndFormatMsg(true, "messages.setHeartsConfirm", "&7You successfully %option% &c%player%' hearts to &7%amount% hearts!",
                new MessageUtils.Replaceable("%option%", optionTwo), new MessageUtils.Replaceable("%player%", concatenatedPlayerNames), new MessageUtils.Replaceable("%amount%", finalAmount + ""));
        sender.sendMessage(setHeartsConfirmMessage);

        return true;
    }

    @Override
    public String getUsage() {
        return "/lifestealz hearts <add | set | remove> <player> [amount]";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.admin.setlife");
    }
}
