package org.strassburger.lifestealz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.RecipeManager;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration config = LifeStealZ.getInstance().getConfig();
        PlayerDataStorage playerDataStorage = LifeStealZ.getInstance().getPlayerDataStorage();

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.versionMsg", "FALLBACK&7You are using version %version%", new Replaceable("%version%", LifeStealZ.getInstance().getDescription().getVersion())));
            return false;
        }

        String optionOne = args[0];

        if (optionOne.equals("reload")) {
            if (!sender.hasPermission("lifestealz.admin.reload")) {
                throwPermissionError(sender);
                return false;
            }

            LifeStealZ.getInstance().reloadConfig();
            config = LifeStealZ.getInstance().getConfig();
            LifeStealZ.getInstance().getLanguageManager().reload();
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reloadMsg", "&7Successfully reloaded the plugin!"));
            return true;
        }

        if (optionOne.equals("help")) {
            if (!sender.hasPermission("lifestealz.help")) {
                throwPermissionError(sender);
                return false;
            }

            String helpMessage = "<reset><!i><!b> \n&8----------------------------------------------------\n&c&lLifeStealZ &7help page<!b>\n&8----------------------------------------------------";
            helpMessage += "\n&c<click:SUGGEST_COMMAND:/lifestealz help>/lifestealz help</click> &8- &7open this menu";
            if (sender.hasPermission("lifestealz.admin.reload"))
                helpMessage += "\n&c<click:SUGGEST_COMMAND:/lifestealz reload>/lifestealz reload</click> &8- &7reload the config";
            if (sender.hasPermission("lifestealz.admin.setlife"))
                helpMessage += "\n&c<click:SUGGEST_COMMAND:/lifestealz hearts>/lifestealz hearts</click> &8- &7modify how many hearts a player has";
            if (sender.hasPermission("lifestealz.admin.giveitem"))
                helpMessage += "\n&c<click:SUGGEST_COMMAND:/lifestealz giveItem>/lifestealz giveItem</click> &8- &7give other players custom items, such as hearts";
            if (sender.hasPermission("lifestealz.viewrecipes"))
                helpMessage += "\n&c<click:SUGGEST_COMMAND:/lifestealz recipe>/lifestealz recipe</click> &8- &7view all recipes";
            if (sender.hasPermission("lifestealz.admin.revive"))
                helpMessage += "\n&c<click:SUGGEST_COMMAND:/revive>/revive</click> &8- &7revive a player without a revive item";
            if (sender.hasPermission("lifestealz.admin.eliminate"))
                helpMessage += "\n&c<click:SUGGEST_COMMAND:/eliminate>/eliminate</click> &8- &7eliminate a player";
            if (sender.hasPermission("lifestealz.withdraw")) helpMessage += "\n&c<click:SUGGEST_COMMAND:/withdrawheart>/withdrawheart</click> &8- &7withdraw a heart";
            helpMessage += "\n&8----------------------------------------------------\n<reset><!i><!b> ";

            Component helpMessageFormatted = MessageUtils.formatMsg(helpMessage);

            sender.sendMessage(helpMessageFormatted);
        }

        if (optionOne.equals("recipe")) {
            if (!sender.hasPermission("lifestealz.viewrecipes")) {
                throwPermissionError(sender);
                return false;
            }

            if (!(sender instanceof Player)) return false;

            if (args.length < 2) {
                throwRecipeUsageError(sender);
                return false;
            }

            String recipe = args[1];

            if (recipe == null || !RecipeManager.getRecipeIds().contains(recipe)) {
                throwRecipeUsageError(sender);
                return false;
            }

            if (!RecipeManager.isCraftable(recipe)) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.recipeNotCraftable", "&cThis item is not craftable!"));
                return false;
            }

            RecipeManager.renderRecipe((Player) sender, recipe);
        }

        String optionTwo = null;

        if (optionOne.equals("hearts")) {
            if (!sender.hasPermission("lifestealz.admin.setlife")) {
                throwPermissionError(sender);
                return false;
            }

            if (args.length < 3) {
                throwUsageError(sender);
                return false;
            }

            optionTwo = args[1];
            List<String> possibleOptionTwo = List.of("add", "set", "remove", "get");

            if (optionTwo == null || !possibleOptionTwo.contains(optionTwo)) {
                throwUsageError(sender);
                return false;
            }

            String targetPlayerName = args[2];

            if (targetPlayerName == null) {
                throwUsageError(sender);
                return false;
            }

            OfflinePlayer targetPlayer = LifeStealZ.getInstance().getServer().getOfflinePlayer(targetPlayerName);

            if (targetPlayer.getName() == null) {
                throwUsageError(sender);
                return false;
            }

            PlayerData targetPlayerData = playerDataStorage.load(targetPlayer.getUniqueId());

            if (targetPlayerData == null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
                return false;
            }

            if (optionTwo.equals("get")) {
                int hearts = (int) (targetPlayerData.getMaxhp() / 2);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.getHearts", "&c%player% &7currently has &c%amount% &7hearts!", new Replaceable("%player%", targetPlayer.getName()), new Replaceable("%amount%", hearts + "")));
                return true;
            }

            int amount = Integer.parseInt(args[3]);

            if (amount < 0) {
                throwUsageError(sender);
                return false;
            }

            int finalAmount = amount;

            switch (optionTwo) {
                case "add": {
                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && targetPlayerData.getMaxhp() + (amount * 2) > config.getInt("maxHearts") * 2) {
                        Component maxHeartsMsg = MessageUtils.getAndFormatMsg(true, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new Replaceable("%limit%", config.getInt("maxHearts") + ""));
                        sender.sendMessage(maxHeartsMsg);
                        return false;
                    }

                    targetPlayerData.setMaxhp(targetPlayerData.getMaxhp() + (amount * 2));
                    playerDataStorage.save(targetPlayerData);
                    LifeStealZ.setMaxHealth(targetPlayer, targetPlayerData.getMaxhp());
                    finalAmount = (int) (targetPlayerData.getMaxhp() / 2);
                    break;
                }
                case "set": {
                    if (amount == 0) {
                        sender.sendMessage(Component.text("§cYou cannot set the lives below or to zero"));
                        return false;
                    }

                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && amount > config.getInt("maxHearts")) {
                        Component maxHeartsMsg = MessageUtils.getAndFormatMsg(true, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", new Replaceable("%limit%", config.getInt("maxHearts") + ""));
                        sender.sendMessage(maxHeartsMsg);
                        return false;
                    }

                    targetPlayerData.setMaxhp(amount * 2);
                    playerDataStorage.save(targetPlayerData);
                    LifeStealZ.setMaxHealth(targetPlayer, targetPlayerData.getMaxhp());
                    break;
                }
                case "remove": {
                    if ((targetPlayerData.getMaxhp() / 2) - (double) amount <= 0) {
                        sender.sendMessage(Component.text("§cYou cannot set the lives below or to zero"));
                        return false;
                    }

                    targetPlayerData.setMaxhp(targetPlayerData.getMaxhp() - (amount * 2));
                    playerDataStorage.save(targetPlayerData);
                    LifeStealZ.setMaxHealth(targetPlayer, targetPlayerData.getMaxhp());
                    finalAmount = (int) (targetPlayerData.getMaxhp() / 2);
                    break;
                }
            }

            Component setHeartsConfirmMessage = MessageUtils.getAndFormatMsg(true, "messages.setHeartsConfirm", "&7You successfully %option% &c%player%' hearts to &7%amount% hearts!", new Replaceable("%option%", optionTwo), new Replaceable("%player%", targetPlayer.getName()), new Replaceable("%amount%", finalAmount + ""));
            sender.sendMessage(setHeartsConfirmMessage);
        }

        if (optionOne.equals("giveItem")) {
            if (!sender.hasPermission("lifestealz.admin.giveitem")) {
                throwPermissionError(sender);
                return false;
            }

            if (args.length < 3) {
                throwGiveItemUsageError(sender);
                return false;
            }

            String targetPlayerName = args[1];

            if (targetPlayerName == null) {
                throwUsageError(sender);
                return false;
            }

            Player targetPlayer = LifeStealZ.getInstance().getServer().getPlayer(targetPlayerName);

            if (targetPlayer == null) {
                throwUsageError(sender);
                return false;
            }

            String item = args[2];

            if (item == null) {
                throwGiveItemUsageError(sender);
                return false;
            }

            Set<String> possibleItems = RecipeManager.getRecipeIds();

            if (!possibleItems.contains(item)) {
                throwGiveItemUsageError(sender);
                return false;
            }

            int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;

            if (amount < 1) {
                throwGiveItemUsageError(sender);
                return false;
            }

            boolean silent = args.length > 4 && args[4].equals("silent");

            targetPlayer.getInventory().addItem(CustomItemManager.createCustomItem(item, amount));
            if (!silent) targetPlayer.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.giveItem", "&7You received &c%amount% &7%item%!", new Replaceable("%amount%", amount + ""), new Replaceable("%item%", CustomItemManager.getCustomItemData(item).getName())));
        }

        if (optionOne.equals("data")) {
            if (!sender.hasPermission("lifestealz.managedata")) {
                throwPermissionError(sender);
                return false;
            }

            if (args.length < 3) {
                throwDataUsageError(sender);
                return false;
            }

            optionTwo = args[1];

            String fileName = args[2];

            if (optionTwo.equals("export")) {
                playerDataStorage.export(fileName);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.exportData", "&7Successfully exported player data to &c%file%.csv", new Replaceable("%file%", fileName)));
            } else if (optionTwo.equals("import")) {
                playerDataStorage.importData(fileName);
                sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.importData", "&7Successfully imported &c%file%.csv&7!", new Replaceable("%file%", fileName)));
            } else {
                throwUsageError(sender);
            }
        }

        return false;
    }

    private void throwUsageError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", "/lifestealz hearts <add | set | remove> <player> [amount]"));
        sender.sendMessage(msg);
    }

    private void throwDataUsageError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", "/lifestealz data <import | export> <file>"));
        sender.sendMessage(msg);
    }

    private void throwGiveItemUsageError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", "/lifestealz giveItem <player> <item> [amount]"));
        sender.sendMessage(msg);
    }

    private void throwRecipeUsageError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", new Replaceable("%usage%", "/lifestealz recipe <" + String.join(" | ", RecipeManager.getRecipeIds()) + ">"));
        sender.sendMessage(msg);
    }

    private void throwPermissionError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!");
        sender.sendMessage(msg);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> availableoptions = new java.util.ArrayList<>(List.of());
            if (sender.hasPermission("lifestealz.admin.reload")) availableoptions.add("reload");
            if (sender.hasPermission("lifestealz.admin.setlife")) availableoptions.add("hearts");
            if (sender.hasPermission("lifestealz.admin.giveitem")) availableoptions.add("giveItem");
            if (sender.hasPermission("lifestealz.viewrecipes")) availableoptions.add("recipe");
            if (sender.hasPermission("lifestealz.help")) availableoptions.add("help");
            if (sender.hasPermission("lifestealz.managedata")) availableoptions.add("data");
            return availableoptions;
        } else if (args.length == 2) {
            if (args[0].equals("hearts")) {
                return List.of("add", "set", "remove", "get");
            } else if (args[0].equals("giveItem")) {
                return null;
            } else if (args[0].equals("recipe")) {
                return new ArrayList<String>(RecipeManager.getRecipeIds());
            } else if (args[0].equals("data") && sender.hasPermission("lifestealz.managedata")) {
                return List.of("export", "import");
            }
        } else if (args.length == 3) {
            if (args[0].equals("hearts")) {
                return null;
            } else if (args[0].equals("giveItem")) {
                return new ArrayList<String>(RecipeManager.getRecipeIds());
            } else if (args[0].equals("data") && args[1].equals("import") && sender.hasPermission("lifestealz.managedata")) {
                return getCSVFiles();
            }
        } else if (args.length == 4) {
            if (args[0].equals("hearts") || args[0].equals("giveItem")) {
                return List.of("1", "32", "64");
            }
        } else if (args.length == 5) {
            if (args[0].equals("giveItem")) {
                return List.of("silent");
            }
        }
        return null;
    }

    private List<String> getCSVFiles() {
        List<String> csvFiles = new ArrayList<>();
        File pluginFolder = LifeStealZ.getInstance().getDataFolder();
        File[] files = pluginFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files != null) {
            for (File file : files) {
                csvFiles.add(file.getName());
            }
        }
        return csvFiles;
    }
}
