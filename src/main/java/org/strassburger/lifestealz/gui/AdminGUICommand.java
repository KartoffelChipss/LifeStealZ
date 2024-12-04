package org.strassburger.lifestealz.gui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.strassburger.lifestealz.LifeStealZ;

public class AdminGUICommand implements CommandExecutor {
    private final LifeStealZ plugin;
    private final GUIManager guiManager;

    public AdminGUICommand(LifeStealZ plugin) {
        this.plugin = plugin;
        this.guiManager = new GUIManager(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        guiManager.openGui((Player) sender);
        return true;

    }
}
