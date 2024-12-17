package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;

import static org.strassburger.lifestealz.util.commands.CommandUtils.throwUsageError;

public class RecipeSubCommand implements SubCommand {
    private final LifeStealZ plugin;

    public RecipeSubCommand(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }

        if (!(sender instanceof Player)) return false;

        if (args.length < 2) {
            throwUsageError(sender, getUsage());
            return false;
        }

        String recipe = args[1];

        if (recipe == null || !plugin.getRecipeManager().getRecipeIds().contains(recipe)) {
            throwUsageError(sender, getUsage());
            return false;
        }

        if (!plugin.getRecipeManager().isCraftable(recipe)) {
            sender.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.recipeNotCraftable", "&cThis item is not craftable!"));
            return false;
        }

        plugin.getRecipeManager().renderRecipe((Player) sender, recipe);
        return true;
    }

    @Override
    public String getUsage() {
        return "/lifestealz recipe <" + String.join(" | ", plugin.getRecipeManager().getRecipeIds()) + ">";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.viewrecipes");
    }
}
