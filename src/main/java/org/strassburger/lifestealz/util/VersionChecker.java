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
    public String MODRINTH_PROJECT_URL = "https://api.modrinth.com/v2/project/l8Uv7FzS";
    public boolean NEW_VERSION_AVAILABLE = false;
    public Logger logger = LifeStealZ.getInstance().getLogger();

    public VersionChecker() {
        String latestVersion = getLatestVersionFromModrinth();
        if (latestVersion != null) {
            String currentVersion = LifeStealZ.getInstance().getDescription().getVersion();
            if (!latestVersion.trim().equals(currentVersion.trim())) {
                NEW_VERSION_AVAILABLE = true;
                logger.info("A new version of LifestealZ is available! Version: " + latestVersion + "\nDownload the latest version here: https://modrinth.com/plugin/lifestealz/versions");
            }
        }
    }

    public String getLatestVersionFromModrinth() {
        try {
            URL projectUrl = new URL(MODRINTH_PROJECT_URL);
            HttpURLConnection projectConnection = (HttpURLConnection) projectUrl.openConnection();
            projectConnection.setRequestMethod("GET");

            if (projectConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader projectReader = new BufferedReader(new InputStreamReader(projectConnection.getInputStream()));
                StringBuilder projectResponse = new StringBuilder();
                String projectInputLine;
                while ((projectInputLine = projectReader.readLine()) != null) {
                    projectResponse.append(projectInputLine);
                }
                projectReader.close();

                JSONParser parser = new JSONParser();
                JSONObject projectJson = (JSONObject) parser.parse(projectResponse.toString());
                JSONArray versionArray = (JSONArray) projectJson.get("versions");
                String latestVersionId = (String) versionArray.get(versionArray.size() - 1);

                URL versionUrl = new URL(MODRINTH_PROJECT_URL + "/version/" + latestVersionId);
                HttpURLConnection versionConnection = (HttpURLConnection) versionUrl.openConnection();
                versionConnection.setRequestMethod("GET");

                if (versionConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader versionReader = new BufferedReader(new InputStreamReader(versionConnection.getInputStream()));
                    StringBuilder versionResponse = new StringBuilder();
                    String versionInputLine;
                    while ((versionInputLine = versionReader.readLine()) != null) {
                        versionResponse.append(versionInputLine);
                    }
                    versionReader.close();

                    JSONObject versionJson = (JSONObject) parser.parse(versionResponse.toString());
                    return (String) versionJson.get("version_number");
                } else {
                    logger.warning("Failed to retrieve version details from Modrinth. Response code: " + versionConnection.getResponseCode());
                }
            } else {
                logger.warning("Failed to retrieve project information from Modrinth. Response code: " + projectConnection.getResponseCode());
            }
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.warning("Failed to check for updates: " + e.getMessage());
        }
        return null;
    }
}
