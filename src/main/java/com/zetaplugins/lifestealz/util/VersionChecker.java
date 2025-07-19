package com.zetaplugins.lifestealz.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.zetaplugins.lifestealz.LifeStealZ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * VersionChecker is a utility class that checks for updates of the LifeStealZ plugin
 * by querying the Modrinth API for the latest version compatible with the current Minecraft version.
 */
public final class VersionChecker {
    private final LifeStealZ plugin;
    private final Logger logger;
    private final String modrinthProjectId;
    private boolean newVersionAvailable = false;

    public VersionChecker(LifeStealZ plugin, String modrinthProjectId) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.modrinthProjectId = modrinthProjectId;
        checkForUpdates();
    }

    private String getModrinthProjectUrl() {
        return "https://api.modrinth.com/v2/project/" + modrinthProjectId;
    }

    /**
     * Checks for updates of the LifeStealZ plugin by comparing the current version with the latest version available on Modrinth.
     * If a new version is available, it logs a message to the console with the details.
     */
    private void checkForUpdates() {
        String latestVersion = fetchLatestVersion();
        if (latestVersion != null) {
            String currentVersion = plugin.getDescription().getVersion();
            System.out.println("Current version: " + currentVersion);
            System.out.println("Latest version: " + latestVersion);
            if (!latestVersion.trim().equals(currentVersion.trim())) {
                newVersionAvailable = true;

                final String reset = "\u001B[0m";
                final String bold = "\u001B[1m";
                final String darkGray = "\u001B[90m";
                final String lightGray = "\u001B[37m";
                final String red = "\u001B[31m";

                String message = "\n" +
                        darkGray + "==========================================" + reset + "\n" +
                        bold + "A new version of LifeStealZ is available!" + reset + "\n" +
                        bold + "New Version: " + reset + bold + red + latestVersion + reset + lightGray + " (Your version: " + currentVersion + ")" + reset + "\n" +
                        bold + "Download here: " + reset + lightGray + reset + "https://modrinth.com/plugin/lifestealz/version/" + latestVersion + "\n" +
                        darkGray + "==========================================" + reset;

                logger.info(message);
            }
        }
    }

    /**
     * Fetches the latest version of the plugin from Modrinth.
     *
     * @return The latest version number as a String, or null if it could not be fetched.
     */
    private String fetchLatestVersion() {
        String mcVersion = plugin.getServer().getMinecraftVersion();
        String encodedGameVersion = URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8);
        String versionsUrl = getModrinthProjectUrl() + "/version?game_versions=" + encodedGameVersion;

        JSONArray versionsArray = fetchJsonArrayFromUrl(versionsUrl);
        if (versionsArray == null || versionsArray.isEmpty()) return null;

        JSONObject latestVersion = (JSONObject) versionsArray.get(0);
        return (String) latestVersion.get("version_number");
    }

    private String fetchVersionNumber(String versionId) {
        String versionUrl = getModrinthProjectUrl() + "/version/" + versionId;
        JSONObject versionJson = fetchJsonFromUrl(versionUrl);
        return versionJson != null ? (String) versionJson.get("version_number") : null;
    }

    /**
     * Fetches JSON data from a given URL.
     * @param urlString The URL to fetch the JSON data from.
     * @return A JSONObject containing the parsed JSON data, or null if an error occurs.
     */
    private JSONObject fetchJsonFromUrl(String urlString) {
        try {
            HttpURLConnection connection = createHttpConnection(urlString);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                return (JSONObject) new JSONParser().parse(response);
            } else {
                logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
            }
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.warning("Error fetching data: " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches a JSON array from a given URL.
     * @param urlString The URL to fetch the JSON array from.
     * @return A JSONArray containing the parsed JSON data, or null if an error occurs.
     */
    private JSONArray fetchJsonArrayFromUrl(String urlString) {
        try {
            HttpURLConnection connection = createHttpConnection(urlString);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                return (JSONArray) new JSONParser().parse(response);
            } else {
                logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
            }
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.warning("Error fetching data: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates an HTTP connection to the specified URL.
     * @param urlString The URL to connect to.
     * @return An HttpURLConnection object for the specified URL.
     * @throws IOException If an I/O error occurs while opening the connection.
     */
    private HttpURLConnection createHttpConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    /**
     * Reads the response from the given HttpURLConnection.
     * @param connection The HttpURLConnection to read the response from.
     * @return The response as a String.
     * @throws IOException If an I/O error occurs while reading the response.
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Checks if a new version of the plugin is available.
     * @return true if a new version is available, false otherwise.
     */
    public boolean isNewVersionAvailable() {
        return newVersionAvailable;
    }
}