package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.Storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.strassburger.lifestealz.util.commands.CommandUtils.*;

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
            OfflinePlayer player = parseOfflinePlayer(args[2], false, true, plugin).get(0);

            if (player == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "playerNotFound", "&cPlayer not found!"));
                return false;
            }

            PlayerData playerData = plugin.getStorage().load(player.getUniqueId());

            int hearts = (int) (playerData.getMaxHealth() / 2);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "getHearts", "&c%player% &7currently has &c%amount% &7hearts!",
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

        List<OfflinePlayer> targetPlayers = parseOfflinePlayer(args[2], true, true, plugin);

        for (OfflinePlayer targetPlayer : targetPlayers) {
            if (targetPlayer == null && targetPlayers.size() == 1) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "playerNotFound",
                        "&cPlayer not found!"
                ));
                return false;
            }

            if (targetPlayer == null && targetPlayers.size() > 1) continue;

            assert targetPlayer != null;
            PlayerData targetPlayerData = plugin.getStorage().load(targetPlayer.getUniqueId());

            switch (optionTwo) {
                case "add": {
                    if (
                            config.getBoolean("enforceMaxHeartsOnAdminCommands")
                            && targetPlayerData.getMaxHealth() + (amount * 2) > config.getInt("maxHearts") * 2
                    ) {
                        sendHeartLimitReachedMessage(sender);
                        return false;
                    }

                    targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() + (amount * 2));
                    storage.save(targetPlayerData);
                    if (targetPlayer instanceof Player) LifeStealZ.setMaxHealth((Player) targetPlayer, targetPlayerData.getMaxHealth());
                    break;
                }
                case "set": {
                    if (amount == 0) {
                        sender.sendMessage(MessageUtils.getAndFormatMsg(
                                false,
                                "connotSetHeartsBelowOrToZero",
                                "&cYou can't set a player's hearts below or to 0!"
                        ));
                        return false;
                    }

                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && amount > config.getInt("maxHearts")) {
                        sendHeartLimitReachedMessage(sender);
                        return false;
                    }

                    targetPlayerData.setMaxHealth(amount * 2);
                    storage.save(targetPlayerData);
                    if (targetPlayer instanceof Player) LifeStealZ.setMaxHealth((Player) targetPlayer, targetPlayerData.getMaxHealth());
                    break;
                }
                case "remove": {
                    if ((targetPlayerData.getMaxHealth() / 2) - (double) amount <= 0) {
                        sender.sendMessage(MessageUtils.getAndFormatMsg(
                                false,
                                "connotSetHeartsBelowOrToZero",
                                "&cYou can't set a player's hearts below or to 0!"
                        ));
                        return false;
                    }

                    targetPlayerData.setMaxHealth(targetPlayerData.getMaxHealth() - (amount * 2));
                    storage.save(targetPlayerData);
                    if (targetPlayer instanceof Player) LifeStealZ.setMaxHealth((Player) targetPlayer, targetPlayerData.getMaxHealth());
                    break;
                }
            }
        }

        sendConfirmMessage(sender, optionTwo, targetPlayers, amount);
        return true;
    }

    private void sendConfirmMessage(CommandSender sender, String optionTwo, List<OfflinePlayer> targetPlayers, int changedAmount) {
        String messageKey;
        String defaultMessage;
        Map<String, String> replacements = new HashMap<>();

        replacements.put("%amount%", String.valueOf(changedAmount));

        if (targetPlayers.size() == 1) {
            replacements.put("%player%", targetPlayers.get(0).getName());
        } else {
            replacements.put("%pamount%", String.valueOf(targetPlayers.size()));
        }

        switch (optionTwo) {
            case "add":
                messageKey = targetPlayers.size() == 1 ? "addHeartsConfirmSingle" : "addHeartsConfirmMultiple";
                defaultMessage = targetPlayers.size() == 1
                        ? "&7You successfully added &c%amount% &7hearts to &c%player%!"
                        : "&7Successfully added &c%amount% &7hearts to &c%pamount% players";
                break;
            case "set":
                messageKey = targetPlayers.size() == 1 ? "setHeartsConfirmSingle" : "setHeartsConfirmMultiple";
                defaultMessage = targetPlayers.size() == 1
                        ? "&7Successfully set &c%player%'s &7hearts to &c%amount% hearts"
                        : "&7Successfully set &c%pamount% players' &7hearts to &c%amount% hearts";
                break;
            case "remove":
                messageKey = targetPlayers.size() == 1 ? "removeHeartsConfirmSingle" : "removeHeartsConfirmMultiple";
                defaultMessage = targetPlayers.size() == 1
                        ? "&7Successfully removed &c%amount% &7hearts from &c%player%"
                        : "&7Successfully removed &c%amount% &7hearts from &c%pamount% players";
                break;
            default:
                throw new IllegalArgumentException("Invalid option: " + optionTwo);
        }

        Component confirmMessage = createConfirmMessage(messageKey, defaultMessage, replacements);
        sender.sendMessage(confirmMessage);
    }

    private Component createConfirmMessage(String key, String defaultMessage, Map<String, String> replacements) {
        MessageUtils.Replaceable[] replaceables = replacements.entrySet().stream()
                .map(entry -> new MessageUtils.Replaceable(entry.getKey(), entry.getValue()))
                .toArray(MessageUtils.Replaceable[]::new);

        return MessageUtils.getAndFormatMsg(true, key, defaultMessage, replaceables);
    }

    private void sendHeartLimitReachedMessage(CommandSender sender) {
        Component maxHeartsMsg = MessageUtils.getAndFormatMsg(
                true,
                "maxHeartLimitReached",
                "&cYou already reached the limit of %limit% hearts!",
                new MessageUtils.Replaceable("%limit%", config.getInt("maxHearts") + ""));
        sender.sendMessage(maxHeartsMsg);
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
