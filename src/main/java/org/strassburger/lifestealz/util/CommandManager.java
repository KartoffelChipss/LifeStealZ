package org.strassburger.lifestealz.util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.*;
import org.strassburger.lifestealz.gui.AdminGUICommand;

public class CommandManager {
    private final LifeStealZ plugin;

    public CommandManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers all commands
     */
    public void registerCommands() {
        registerCommand("lifestealz", new SettingsCommand(plugin), new SettingsCommand(plugin));
        registerCommand("hearts", new HeartCommand(plugin), new HeartCommand(plugin));
        registerCommand("withdrawheart", new WithdrawCommand(plugin), new WithdrawCommand(plugin));
        registerCommand("revive", new ReviveCommand(plugin), new ReviveCommand(plugin));
        registerCommand("eliminate", new EliminateCommand(plugin), new EliminateCommand(plugin));
        registerCommand("lzgui", new AdminGUICommand(plugin), null);
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
            command.permissionMessage(MessageUtils.getAndFormatMsg(false, "messages.noPermsError", "<red>You do not have permission to execute this command!"));
        }
    }
}
