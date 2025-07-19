package com.zetaplugins.lifestealz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.zetaplugins.lifestealz.LifeStealZ;
import com.zetaplugins.lifestealz.commands.SubCommand;
import com.zetaplugins.lifestealz.util.MessageUtils;
import com.zetaplugins.lifestealz.util.commands.CommandUtils;

import static com.zetaplugins.lifestealz.util.commands.CommandUtils.throwUsageError;

public final class RecipeSubCommand implements SubCommand {
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
        Player player = (Player) sender;

        if (args.length < 2) {
            throwUsageError(player, getUsage());
            return false;
        }

        String itemId = args[1];

        if (itemId == null || !plugin.getRecipeManager().getItemIds().contains(itemId)) {
            throwUsageError(player, getUsage());
            return false;
        }

        if (!plugin.getRecipeManager().isCraftable(itemId)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "recipeNotCraftable",
                    "&cThis item is not craftable!"
            ));
            return false;
        }

        String recipeId = args.length > 2 ? args[2] : null;

        if (recipeId == null) {
            plugin.getRecipeManager().renderRecipe(player, itemId);
            return true;
        }

        plugin.getRecipeManager().renderRecipe(player, itemId, recipeId);
        return true;
    }

    @Override
    public String getUsage() {
        return "/lifestealz recipe <" + String.join(" | ", plugin.getRecipeManager().getItemIds()) + "> [<recipeId>]";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.viewrecipes");
    }
}
