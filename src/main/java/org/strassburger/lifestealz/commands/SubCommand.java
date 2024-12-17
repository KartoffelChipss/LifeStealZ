package org.strassburger.lifestealz.commands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    /**
     * Execute the sub-command logic.
     *
     * @param sender Command sender
     * @param args Arguments passed to the command
     * @return true if successful, false otherwise
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Provides the usage description for the sub-command.
     *
     * @return A string representing command usage
     */
    String getUsage();

    /**
     * Checks if a sender has permission to use the sub-command.
     *
     * @param sender The command sender
     * @return True if the sender has permission
     */
    boolean hasPermission(CommandSender sender);
}
