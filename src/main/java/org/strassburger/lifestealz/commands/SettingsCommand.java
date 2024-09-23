package org.strassburger.lifestealz.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.customitems.CustomItem;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;
import org.strassburger.lifestealz.util.geysermc.GeyserManager;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SettingsCommand implements CommandExecutor, TabCompleter {
    private final LifeStealZ plugin;
    private final FileConfiguration config;
    private final Storage storage;
    private final GeyserManager geyserManager;


    public SettingsCommand(LifeStealZ plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.storage = plugin.getStorage();
        this.geyserManager = plugin.getGeyserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            sendVersionMessage(sender);
            return false;
        }

        String optionOne = args[0];

        switch (optionOne) {
            case "reload":
                return handleReload(sender);
            case "help":
                return handleHelp(sender);
            case "recipe":
                return handleRecipe(sender, args);
            case "hearts":
                return handleHearts(sender, args);
            case "giveItem":
                return handleGiveItem(sender, args);
            case "data":
                return handleData(sender, args);
            case "dev":
                return handleDev(sender, args);
            default:
                return false;
        }
    }

    private void sendVersionMessage(CommandSender sender) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.versionMsg", "FALLBACK&7You are using version %version%",
                new MessageUtils.Replaceable("%version%", plugin.getDescription().getVersion())));
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("lifestealz.admin.reload")) {
            throwPermissionError(sender);
            return false;
        }

        plugin.reloadConfig();
        plugin.getLanguageManager().reload();
        plugin.getRecipeManager().registerRecipes();
        sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.reloadMsg", "&7Successfully reloaded the plugin!"));
        return true;
    }

    private boolean handleHelp(CommandSender sender) {
        if (!sender.hasPermission("lifestealz.help")) {
            throwPermissionError(sender);
            return false;
        }

        StringBuilder helpMessage = new StringBuilder("<reset><!i><!b> \n&8----------------------------------------------------\n&c&lLifeStealZ &7help page<!b>\n&8----------------------------------------------------\n");
        addHelpEntry(helpMessage, sender, "lifestealz.admin.reload", "/lifestealz reload", "- reload the config");
        addHelpEntry(helpMessage, sender, "lifestealz.admin.setlife", "/lifestealz hearts", "- modify how many hearts a player has");
        addHelpEntry(helpMessage, sender, "lifestealz.admin.giveitem", "/lifestealz giveItem", "- give other players custom items, such as hearts");
        addHelpEntry(helpMessage, sender, "lifestealz.viewrecipes", "/lifestealz recipe", "- view all recipes");
        addHelpEntry(helpMessage, sender, "lifestealz.admin.revive", "/revive", "- revive a player without a revive item");
        addHelpEntry(helpMessage, sender, "lifestealz.admin.eliminate", "/eliminate", "- eliminate a player");
        addHelpEntry(helpMessage, sender, "lifestealz.withdraw", "/withdrawheart", "- withdraw a heart");
        helpMessage.append("\n&8----------------------------------------------------\n<reset><!i><!b> ");

        sender.sendMessage(MessageUtils.formatMsg(helpMessage.toString()));
        return true;
    }

    private void addHelpEntry(StringBuilder helpMessage, CommandSender sender, String permission, String command, String description) {
        if (sender.hasPermission(permission)) {
            helpMessage.append("&c<click:SUGGEST_COMMAND:").append(command).append(">").append(command).append("</click> &8- &7").append(description).append("\n");
        }
    }

    private boolean handleRecipe(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifestealz.viewrecipes")) {
            throwPermissionError(sender);
            return false;
        }

        if (!(sender instanceof Player)) return false;

        if (args.length < 2) {
            throwUsageError(sender, "/lifestealz recipe <" + String.join(" | ", plugin.getRecipeManager().getRecipeIds()) + ">");
            return false;
        }

        String recipe = args[1];

        if (recipe == null || !plugin.getRecipeManager().getRecipeIds().contains(recipe)) {
            throwUsageError(sender, "/lifestealz recipe <" + String.join(" | ", plugin.getRecipeManager().getRecipeIds()) + ">");
            return false;
        }

        if (!plugin.getRecipeManager().isCraftable(recipe)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.recipeNotCraftable", "&cThis item is not craftable!"));
            return false;
        }

        plugin.getRecipeManager().renderRecipe((Player) sender, recipe);
        return true;
    }

    private boolean handleHearts(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifestealz.admin.setlife")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 3) {
            throwUsageError(sender, "/lifestealz hearts <add | set | remove> <player> [amount]");
            return false;
        }

        String optionTwo = args[1];
        List<String> possibleOptionTwo = List.of("add", "set", "remove", "get");

        if (optionTwo == null || !possibleOptionTwo.contains(optionTwo)) {
            throwUsageError(sender, "/lifestealz hearts <add | set | remove> <player> [amount]");
            return false;
        }

        String targetPlayerName = args[2];

        if (targetPlayerName == null) {
            throwUsageError(sender, "/lifestealz hearts <add | set | remove> <player> [amount]");
            return false;
        }

        PlayerData targetPlayerData = null;

        if(plugin.hasGeyser() && plugin.getGeyserPlayerFile().isPlayerStored(targetPlayerName)) {
            targetPlayerData = storage.load(geyserManager.getOfflineBedrockPlayerUniqueId(targetPlayerName));
        } else {
            Player onlinePlayer = plugin.getServer().getPlayer(targetPlayerName);
            UUID targetUUID = (onlinePlayer != null) ? onlinePlayer.getUniqueId() : plugin.getServer().getOfflinePlayer(targetPlayerName).getUniqueId();
            OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(targetUUID);

            if (targetPlayer.getName() == null) {
                throwUsageError(sender, "/lifestealz hearts <add | set | remove> <player> [amount]");
                return false;
            }

            targetPlayerData = storage.load(targetPlayer.getUniqueId());
        }

        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

        if (targetPlayerData == null) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"));
            return false;
        }

        if (optionTwo.equals("get")) {
            int hearts = (int) (targetPlayerData.getMaxHealth() / 2);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.getHearts", "&c%player% &7currently has &c%amount% &7hearts!",
                    new MessageUtils.Replaceable("%player%", targetPlayerName), new MessageUtils.Replaceable("%amount%", hearts + "")));
            return true;
        }

        int amount = Integer.parseInt(args[3]);

        if (amount < 0) {
            throwUsageError(sender, "/lifestealz hearts <add | set | remove> <player> [amount]");
            return false;
        }

        int finalAmount = amount;

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

        Component setHeartsConfirmMessage = MessageUtils.getAndFormatMsg(true, "messages.setHeartsConfirm", "&7You successfully %option% &c%player%' hearts to &7%amount% hearts!",
                new MessageUtils.Replaceable("%option%", optionTwo), new MessageUtils.Replaceable("%player%", targetPlayerName), new MessageUtils.Replaceable("%amount%", finalAmount + ""));
        sender.sendMessage(setHeartsConfirmMessage);
        return true;
    }

    private boolean handleGiveItem(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifestealz.admin.giveitem")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 3) {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
            return false;
        }

        String targetPlayerName = args[1];

        if (targetPlayerName == null) {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
            return false;
        }

        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
            return false;
        }

        String item = args[2];

        if (item == null) {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
            return false;
        }

        Set<String> possibleItems = plugin.getRecipeManager().getRecipeIds();

        if (!possibleItems.contains(item)) {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
            return false;
        }

        int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;

        if (amount < 1) {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
            return false;
        }

        boolean silent = args.length > 4 && args[4].equals("silent");

        targetPlayer.getInventory().addItem(CustomItemManager.createCustomItem(item, amount));
        if (!silent)
            targetPlayer.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.giveItem", "&7You received &c%amount% &7%item%!",
                    new MessageUtils.Replaceable("%amount%", amount + ""), new MessageUtils.Replaceable("%item%", CustomItemManager.getCustomItemData(item).getName())));
        return true;
    }

    private boolean handleData(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifestealz.managedata")) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 3) {
            throwUsageError(sender, "/lifestealz data <import | export> <file>");
            return false;
        }

        String optionTwo = args[1];
        String fileName = args[2];

        if (optionTwo.equals("export")) {
            storage.export(fileName);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.exportData", "&7Successfully exported player data to &c%file%.csv",
                    new MessageUtils.Replaceable("%file%", fileName)));
        } else if (optionTwo.equals("import")) {
            storage.importData(fileName);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.importData", "&7Successfully imported &c%file%.csv&7!\n&cPlease restart the server, to ensure flawless migration!",
                    new MessageUtils.Replaceable("%file%", fileName)));
        } else {
            throwUsageError(sender, "/lifestealz giveItem <player> <item> [amount]");
        }
        return true;
    }

    private boolean handleDev(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            throwPermissionError(sender);
            return false;
        }

        if (args.length < 2) {
            throwUsageError(sender, "/lifestealz dev <giveForbiddenitem>");
            return false;
        }

        String optionTwo = args[1];

        if (optionTwo.equals("giveForbiddenitem")) {
            if (!(sender instanceof Player)) return false;

            Player player = (Player) sender;

            player.getInventory().addItem(new CustomItem(Material.BARRIER).makeForbidden().getItemStack());

            return true;
        }

        return false;
    }

    private void throwUsageError(CommandSender sender, String usage) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%",
                new MessageUtils.Replaceable("%usage%", usage));
        sender.sendMessage(msg);
    }

    private void throwPermissionError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!");
        sender.sendMessage(msg);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String>
                    availableOptions = new ArrayList<>();
            if (sender.hasPermission("lifestealz.admin.reload")) availableOptions.add("reload");
            if (sender.hasPermission("lifestealz.admin.setlife")) availableOptions.add("hearts");
            if (sender.hasPermission("lifestealz.admin.giveitem")) availableOptions.add("giveItem");
            if (sender.hasPermission("lifestealz.viewrecipes")) availableOptions.add("recipe");
            if (sender.hasPermission("lifestealz.help")) availableOptions.add("help");
            if (sender.hasPermission("lifestealz.managedata")) availableOptions.add("data");
            return availableOptions;
        } else if (args.length == 2) {
            if (args[0].equals("hearts")) {
                return List.of("add", "set", "remove", "get");
            } else if (args[0].equals("giveItem")) {
                return null; // Suggest online player names
            } else if (args[0].equals("recipe")) {
                return new ArrayList<>(plugin.getRecipeManager().getRecipeIds());
            } else if (args[0].equals("data") && sender.hasPermission("lifestealz.managedata")) {
                return List.of("export", "import");
            } else if (args[0].equals("dev")) {
                return List.of("giveForbiddenitem");
            }
        } else if (args.length == 3) {
            if (args[0].equals("hearts")) {
                return null; // Suggest online player names
            } else if (args[0].equals("giveItem")) {
                return new ArrayList<>(plugin.getRecipeManager().getRecipeIds());
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
        File pluginFolder = plugin.getDataFolder();
        File[] files = pluginFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files != null) {
            for (File file : files) {
                csvFiles.add(file.getName());
            }
        }
        return csvFiles;
    }
}
