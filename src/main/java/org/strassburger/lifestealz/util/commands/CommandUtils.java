package org.strassburger.lifestealz.util.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandUtils {
    /**
     * Parses a player name (or a star) to a list of players
     * @param playerName The name of the player
     * @param allowStar If true, the star will be allowed as a wildcard
     * @param plugin The plugin instance
     * @return A list of players (or an empty list if the player is not online)
     */
    public static List<Player> parsePlayerName(String playerName, boolean allowStar, LifeStealZ plugin) {
        List<Player> players = new ArrayList<>();

        if (playerName.equals("*") && allowStar) {
            players.addAll(plugin.getServer().getOnlinePlayers());
        } else {
            Player player = plugin.getServer().getPlayer(playerName);
            players.add(player);
        }

        return players;
    }

    /**
     * Parses a player name (or a star) to a list of players
     * @param playerName The name of the player
     * @param allowStar If true, the star will be allowed as a wildcard
     * @return A list of players (or an empty list if the player is not online)
     */
    public static List<Player> parsePlayerName(String playerName, boolean allowStar) {
        return parsePlayerName(playerName, allowStar, LifeStealZ.getInstance());
    }

    /**
     * Gets a list of player names for tab completion
     * @param allowStar If true, the star will be allowed as a wildcard
     * @param plugin The plugin instance
     * @return A list of player names
     */
    public static List<String> getPlayersTabCompletion(boolean allowStar, LifeStealZ plugin) {
        List<String> playerNames = new ArrayList<>();
        if (allowStar) playerNames.add("*");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    /**
     * Gets a list of player names for tab completion
     * @param allowStar If true, the star will be allowed as a wildcard
     * @return A list of player names
     */
    public static List<String> getPlayersTabCompletion(boolean allowStar) {
        return getPlayersTabCompletion(allowStar, LifeStealZ.getInstance());
    }

    public static void throwUsageError(CommandSender sender, String usage) {
        Component msg = MessageUtils.getAndFormatMsg(false, "usageError", "&cUsage: %usage%",
                new MessageUtils.Replaceable("%usage%", usage));
        sender.sendMessage(msg);
    }

    public static void throwPermissionError(CommandSender sender) {
        Component msg = MessageUtils.getAndFormatMsg(false, "noPermissionError", "&cYou don't have permission to use this!");
        sender.sendMessage(msg);
    }
}
