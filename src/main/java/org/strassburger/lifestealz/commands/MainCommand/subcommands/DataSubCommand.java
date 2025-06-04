package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;
import org.strassburger.lifestealz.storage.Storage;

import static org.strassburger.lifestealz.util.commands.CommandUtils.throwUsageError;

public final class DataSubCommand implements SubCommand {
    private final Storage storage;
    private final LifeStealZ plugin;

    public DataSubCommand(LifeStealZ plugin) {
        this.storage = plugin.getStorage();
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender);
            return false;
        }

        if (args.length < 3) {
            throwUsageError(sender, "/lifestealz data <import | export> <file>");
            return false;
        }

        String optionTwo = args[1];
        String fileName = args[2];

        if (optionTwo.equals("export")) {
            return handleExport(sender, fileName);
        } else if (optionTwo.equals("import")) {
            return handleImport(sender, fileName);
        } else {
            throwUsageError(sender, getUsage());
        }
        return true;
    }

    private boolean handleExport(CommandSender sender, String fileName) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "exportingData",
                "&7Exporting player data..."
        ));
        Runnable runnable = () -> {
            String filePath = storage.export(fileName);
            if (filePath != null) {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        true,
                        "exportData",
                        "&7Successfully exported player data to &c%file%",
                        new MessageUtils.Replaceable("%file%", filePath)
                ));
            } else {
                sender.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "exportDataError",
                        "&cFailed to export data! Check console for details."
                ));
            }
        };
        if (LifeStealZ.getFoliaLib().isFolia()) {
            WrappedTask wrappedTask1 = (WrappedTask) LifeStealZ.getFoliaLib().getScheduler().runAsync(wrappedTask -> runnable.run());
            plugin.getAsyncTaskManager().addTask(null, wrappedTask1);
        } else {
            BukkitTask task = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
            plugin.getAsyncTaskManager().addTask(task, null);
        }
        return true;
    }

    private boolean handleImport(CommandSender sender, String fileName) {
        sender.sendMessage(MessageUtils.getAndFormatMsg(
                true,
                "importingData",
                "&7Importing player data..."
        ));
        Runnable runnable = () -> {
            storage.importData(fileName);
            sender.sendMessage(MessageUtils.getAndFormatMsg(
                    true,
                    "importData",
                    "&7Successfully imported &c%file%.csv&7!\n&cPlease restart the server, to ensure flawless migration!",
                    new MessageUtils.Replaceable("%file%", fileName)
            ));
        };
        if (LifeStealZ.getFoliaLib().isFolia()) {
            WrappedTask wrappedTask1 = (WrappedTask) LifeStealZ.getFoliaLib().getScheduler().runAsync(wrappedTask -> runnable.run());
            plugin.getAsyncTaskManager().addTask(null, wrappedTask1);
        } else {
            BukkitTask task = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
            plugin.getAsyncTaskManager().addTask(task, null);
        }
        return true;
    }

    @Override
    public String getUsage() {
        return "/lifestealz data <import | export> <file>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("lifestealz.managedata");
    }
}
