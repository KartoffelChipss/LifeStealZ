package org.strassburger.lifestealz.commands.MainCommand.subcommands;

import org.bukkit.command.CommandSender;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.commands.SubCommand;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.commands.CommandUtils;
import org.strassburger.lifestealz.util.storage.Storage;

import static org.strassburger.lifestealz.util.commands.CommandUtils.throwUsageError;

public class DataSubCommand implements SubCommand {
    private final Storage storage;

    public DataSubCommand(LifeStealZ plugin) {
        this.storage = plugin.getStorage();
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
            storage.export(fileName);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.exportData", "&7Successfully exported player data to &c%file%.csv",
                    new MessageUtils.Replaceable("%file%", fileName)));
        } else if (optionTwo.equals("import")) {
            storage.importData(fileName);
            sender.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.importData", "&7Successfully imported &c%file%.csv&7!\n&cPlease restart the server, to ensure flawless migration!",
                    new MessageUtils.Replaceable("%file%", fileName)));
        } else {
            throwUsageError(sender, getUsage());
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
