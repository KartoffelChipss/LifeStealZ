package org.strassburger.lifestealz.util;

public class TimeFormatter {
    public static String formatDuration(long duration) {
        if (duration <= 0) return "0s";

        final long SECOND_MS = 1000;
        final long MINUTE_MS = 60 * SECOND_MS;
        final long HOUR_MS = 60 * MINUTE_MS;
        final long DAY_MS = 24 * HOUR_MS;

        long days = duration / DAY_MS;
        duration %= DAY_MS;

        long hours = duration / HOUR_MS;
        duration %= HOUR_MS;

        long minutes = duration / MINUTE_MS;
        duration %= MINUTE_MS;

        long seconds = duration / SECOND_MS;

        StringBuilder result = new StringBuilder();
        int addedSymbols = 0;

        if (days > 0) {
            result.append(days).append("d");
            addedSymbols++;
        }
        if (hours > 0 && addedSymbols < 2) {
            if (result.length() > 0) result.append(" ");
            result.append(hours).append("h");
            addedSymbols++;
        }
        if (minutes > 0 && addedSymbols < 2) {
            if (result.length() > 0) result.append(" ");
            result.append(minutes).append("m");
            addedSymbols++;
        }
        if (seconds > 0 && addedSymbols < 2) {
            if (result.length() > 0) result.append(" ");
            result.append(seconds).append("s");
            addedSymbols++;
        }

        return result.length() > 0 ? result.toString() : "0s";
    }
}
