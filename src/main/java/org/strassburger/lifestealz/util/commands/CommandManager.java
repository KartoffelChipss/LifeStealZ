package org.strassburger.lifestealz.util.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.*;
import org.strassburger.lifestealz.commands.MainCommand.MainCommandHandler;
import org.strassburger.lifestealz.commands.MainCommand.MainTabCompleter;
import org.strassburger.lifestealz.util.MessageUtils;

public class CommandManager {
    private final LifeStealZ plugin;

    public CommandManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers all commands
     */
    public void registerCommands() {
        registerCommand("lifestealz", new MainCommandHandler(plugin), new MainTabCompleter(plugin));
        registerCommand("hearts", new HeartCommand(plugin), new HeartCommand(plugin));
        registerCommand("withdrawheart", new WithdrawCommand(plugin), new WithdrawCommand(plugin));
        registerCommand("revive", new ReviveCommand(plugin), new ReviveCommand(plugin));
        registerCommand("eliminate", new EliminateCommand(plugin), new EliminateCommand(plugin));
    }

    /**
     * Registers a command
     *
     * @param name The name of the command
     * @param executor The executor of the command
     * @param tabCompleter The tab completer of the command
     */
    private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = plugin.getCommand(name);

        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(tabCompleter);
            command.permissionMessage(MessageUtils.getAndFormatMsg(false, "noPermsError", "<red>You do not have permission to execute this command!"));
        }
    }
}
