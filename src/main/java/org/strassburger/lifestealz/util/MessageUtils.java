package org.strassburger.lifestealz.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils {
    private MessageUtils() {}

    private static final Map<String, String> colorMap;

    static {
        colorMap = new HashMap<>();
        colorMap.put("&0", "<black>");
        colorMap.put("&1", "<dark_blue>");
        colorMap.put("&2", "<dark_green>");
        colorMap.put("&3", "<dark_aqua>");
        colorMap.put("&4", "<dark_red>");
        colorMap.put("&5", "<dark_purple>");
        colorMap.put("&6", "<gold>");
        colorMap.put("&7", "<gray>");
        colorMap.put("&8", "<dark_gray>");
        colorMap.put("&9", "<blue>");
        colorMap.put("&a", "<green>");
        colorMap.put("&b", "<aqua>");
        colorMap.put("&c", "<red>");
        colorMap.put("&d", "<light_purple>");
        colorMap.put("&e", "<yellow>");
        colorMap.put("&f", "<white>");
        colorMap.put("&k", "<obfuscated>");
        colorMap.put("&l", "<bold>");
        colorMap.put("&m", "<strikethrough>");
        colorMap.put("&n", "<underline>");
        colorMap.put("&o", "<italic>");
        colorMap.put("&r", "<reset>");
    }

    /**
     * Formats a message with placeholders
     *
     * @param msg The message to format
     * @param replaceables The placeholders to replace
     * @return The formatted message
     */
    public static Component formatMsg(String msg, Replaceable... replaceables) {
        for (Replaceable replaceable : replaceables) {
            msg = msg.replace(replaceable.getPlaceholder(), replaceable.getValue());
        }

        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }

        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize("<!i>" + msg);
    }

    /**
     * Gets and formats a message from the config
     *
     * @param addPrefix Whether to add the prefix to the message
     * @param path The path to the message in the config
     * @param fallback The fallback message
     * @param replaceables The placeholders to replace
     * @return The formatted message
     */
    public static Component getAndFormatMsg(boolean addPrefix, String path, String fallback, Replaceable... replaceables) {
        if (path.startsWith("messages.")) path = path.substring("messages.".length());

        MiniMessage mm = MiniMessage.miniMessage();
        String msg = "<!i>" + LifeStealZ.getInstance().getLanguageManager().getString(path, fallback);
        String prefix = LifeStealZ.getInstance().getLanguageManager().getString("prefix", "&8[&cLifeStealZ&8]");
        if (addPrefix) {
            msg = prefix + " " + msg;
        }

        for (Replaceable replaceable : replaceables) {
            msg = msg.replace(replaceable.getPlaceholder(), replaceable.getValue());
        }

        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }

        return mm.deserialize(msg);
    }

    public static String formatTime(long seconds) {
        if (seconds < 0) return "0s";

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder result = new StringBuilder();

        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0 || (hours > 0 && secs > 0)) {
            result.append(minutes).append("m ");
        }
        if (secs > 0 || seconds == 0) {
            result.append(secs).append("s");
        }

        return result.toString().trim();
    }

    public static class Replaceable {
        private final String placeholder;
        private final String value;

        public Replaceable(String placeholder, String value) {
            this.placeholder = placeholder;
            this.value = value;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public String getValue() {
            return value;
        }
    }
}
