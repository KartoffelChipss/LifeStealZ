package org.strassburger.lifestealz.util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.*;

public class CommandManager {
    private static final LifeStealZ plugin = LifeStealZ.getInstance();

    private CommandManager() {}

    public static void registerCommands() {
        registerCommand("lifestealz", new SettingsCommand(), new SettingsCommand());
        registerCommand("hearts", new HeartCommand(), new HeartCommand());
        registerCommand("withdrawheart", new WithdrawCommand(), new WithdrawCommand());
        registerCommand("revive", new ReviveCommand(), new ReviveCommand());
        registerCommand("eliminate", new EliminateCommand(), new EliminateCommand());
    }

    private static void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = plugin.getCommand(name);

        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(tabCompleter);
            command.permissionMessage(MessageUtils.formatMsg("<red>You do not have permission to execute this command."));
        }
    }
}
