package org.strassburger.lifestealz.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.strassburger.lifestealz.LifeStealZ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class VersionChecker {
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

    private void checkForUpdates() {
        String latestVersion = fetchLatestVersion();
        if (latestVersion != null) {
            String currentVersion = plugin.getDescription().getVersion();
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
                        bold + "New Version: " + reset + lightGray + currentVersion + " -> " + bold + red + latestVersion + reset + "\n" +
                        bold + "Download here: " + reset + lightGray + reset + "https://modrinth.com/plugin/lifestealz/version/" + latestVersion + "\n" +
                        darkGray + "==========================================" + reset;

                logger.info(message);
            }
        }
    }

    private String fetchLatestVersion() {
        JSONObject projectJson = fetchJsonFromUrl(getModrinthProjectUrl());
        if (projectJson == null) return null;

        JSONArray versionArray = (JSONArray) projectJson.get("versions");
        if (versionArray == null || versionArray.isEmpty()) return null;

        String latestVersionId = (String) versionArray.get(versionArray.size() - 1);
        return fetchVersionNumber(latestVersionId);
    }

    private String fetchVersionNumber(String versionId) {
        String versionUrl = getModrinthProjectUrl() + "/version/" + versionId;
        JSONObject versionJson = fetchJsonFromUrl(versionUrl);
        return versionJson != null ? (String) versionJson.get("version_number") : null;
    }

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

    private HttpURLConnection createHttpConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

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

    public boolean isNewVersionAvailable() {
        return newVersionAvailable;
    }
}