package com.zetaplugins.lifestealz.util;

import com.zetaplugins.lifestealz.LifeStealZ;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class WebHookManager {
    private final LifeStealZ plugin;

    public WebHookManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    public boolean isEliminationWebhookEnabled() {
        return plugin.getConfig().getBoolean("webhook.elimination");
    }

    public boolean isReviveWebhookEnabled() {
        return plugin.getConfig().getBoolean("webhook.revive");
    }

    private String getWebhookUrl() {
        return plugin.getConfig().getString("webhook.url");
    }

    public void sendWebhookMessage(String title, String message, String colorHex) {
        try {
            URL url = new URL(getWebhookUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the HTTP connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            int color = Integer.parseInt(colorHex, 16);

            // Create JSON payload for Discord Embed
            String jsonPayload = "{"
                    + "\"embeds\": [{"
                    + "\"title\": \"" + title + "\","
                    + "\"description\": \"" + message + "\","
                    + "\"color\": " + color
                    + "}]"
                    + "}";

            // Send the JSON payload to the webhook
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check the response code from Discord
            int responseCode = connection.getResponseCode();
            if (responseCode != 204) plugin.getLogger().severe("Failed to send Discord webhook message. Response code: " + responseCode);

            connection.disconnect();

        } catch (Exception e) {
            plugin.getLogger().severe("Error sending Discord webhook message: " + e.getMessage());
        }
    }

    public void sendWebhookMessage(WebHookType type, String... placeholders) {
        switch (type) {
            case ELIMINATION:
                if (!isEliminationWebhookEnabled()) return;
                sendWebhookMessage(
                        plugin.getLanguageManager().getString("webhookEliminationTitle", "Player Eliminated"),
                        plugin.getLanguageManager().getString("webhookElimination", "**%player%** has been eliminated by **%killer%**!")
                                .replace("%player%", placeholders[0])
                                .replace("%killer%", placeholders[1]),
                        plugin.getLanguageManager().getString("webhookEliminationColor", "ea3323")
                );
                break;
            case REVIVE:
                if (!isReviveWebhookEnabled()) return;
                sendWebhookMessage(
                        plugin.getLanguageManager().getString("webhookReviveTitle", "Player Revived"),
                        plugin.getLanguageManager().getString("webhookRevive", "**%player%** has been revived by **%reviver%**!")
                                .replace("%player%", placeholders[0])
                                .replace("%reviver%", placeholders[1]),
                        plugin.getLanguageManager().getString("webhookReviveColor", "b094ee")
                );
                break;
        }
    }

    public enum WebHookType {
        ELIMINATION,
        REVIVE
    }
}
