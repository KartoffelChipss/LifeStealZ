package org.strassburger.lifestealz.commands.MainCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.strassburger.lifestealz.LifeStealZ;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.strassburger.lifestealz.util.commands.CommandUtils.getPlayersTabCompletion;

public class MainTabCompleter implements TabCompleter {
    private final LifeStealZ plugin;

    public MainTabCompleter(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) return getFirstArgOptions(sender);
        if (args.length == 2) return getSecondArgOptions(sender, args);
        if (args.length == 3) return getThirdArgOptions(sender, args);
        if (args.length == 4) return getFourthArgOptions(args);
        if (args.length == 5) return getFifthArgOptions(args);
        return null;
    }

    private List<String> getFirstArgOptions(CommandSender sender) {
        List<String> options = new ArrayList<>();
        if (sender.hasPermission("lifestealz.admin.reload")) options.add("reload");
        if (sender.hasPermission("lifestealz.admin.setlife")) options.add("hearts");
        if (sender.hasPermission("lifestealz.admin.giveitem")) options.add("giveItem");
        if (sender.hasPermission("lifestealz.viewrecipes")) options.add("recipe");
        if (sender.hasPermission("lifestealz.help")) options.add("help");
        if (sender.hasPermission("lifestealz.managedata")) options.add("data");
        if (sender.hasPermission("lifestealz.graceperiod")) options.add("graceperiod");
        return options;
    }

    private List<String> getSecondArgOptions(CommandSender sender, String[] args) {
        switch (args[0]) {
            case "hearts":
                return List.of("add", "set", "remove", "get");
            case "giveItem":
            case "graceperiod":
                return getPlayersTabCompletion(true, plugin);
            case "recipe":
                return new ArrayList<>(plugin.getRecipeManager().getRecipeIds());
            case "data":
                if (sender.hasPermission("lifestealz.managedata")) return List.of("export", "import");
                break;
            case "dev":
                return List.of("giveForbiddenitem", "isInGracePeriod", "setFirstJoinDate", "refreshCaches");
        }
        return null;
    }

    private List<String> getThirdArgOptions(CommandSender sender, String[] args) {
        switch (args[0]) {
            case "hearts":
                if ("get".equals(args[1])) return getPlayersTabCompletion(false, plugin);
                return getPlayersTabCompletion(true, plugin);
            case "graceperiod":
                return List.of("skip", "reset");
            case "giveItem":
                return new ArrayList<>(plugin.getRecipeManager().getRecipeIds());
            case "data":
                if ("import".equals(args[1]) && sender.hasPermission("lifestealz.managedata")) {
                    return getCSVFiles();
                }
        }
        return null;
    }

    private List<String> getFourthArgOptions(String[] args) {
        if ("hearts".equals(args[0]) || "giveItem".equals(args[0])) {
            return List.of("1", "32", "64");
        }
        return null;
    }

    private List<String> getFifthArgOptions(String[] args) {
        if ("giveItem".equals(args[0])) {
            return List.of("silent");
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
