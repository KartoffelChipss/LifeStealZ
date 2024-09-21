package org.strassburger.lifestealz.util.geysermc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.lifestealz.LifeStealZ;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.Set;

public class GeyserPlayerFile {
    private final JavaPlugin plugin = LifeStealZ.getInstance();
    private File geyserPlayerFile;
    private FileConfiguration geyserPlayerConfig;

    public GeyserPlayerFile() {
        createGeyserPlayerFile();
    }

    private void createGeyserPlayerFile() {
        geyserPlayerFile = new File(plugin.getDataFolder(), "geyser_players.yml");

        if (!geyserPlayerFile.exists()) {
            geyserPlayerFile.getParentFile().mkdirs();
            plugin.saveResource("geyser_players.yml", false);
        }

        geyserPlayerConfig = YamlConfiguration.loadConfiguration(geyserPlayerFile);
    }

    public void savePlayer(UUID uuid, String name) {
        geyserPlayerConfig.set("players." + uuid.toString() + ".name", name);
        saveConfig();
    }

    public String getPlayerName(UUID uuid) {
        return geyserPlayerConfig.getString("players." + uuid.toString() + ".name");
    }

    public UUID getPlayerUUID(String name) {
        if(name == null) return null;
        Set<String> keys = Objects.requireNonNull(geyserPlayerConfig.getConfigurationSection("players")).getKeys(false);
        if(keys != null) {
        for (String uuidString : keys) {
            String storedName = geyserPlayerConfig.getString("players." + uuidString + ".name");
            if (storedName != null && storedName.equalsIgnoreCase(name)) {
                return UUID.fromString(uuidString);
            }
        }
        }
        return null;
    }

    private void saveConfig() {
        try {
            geyserPlayerConfig.save(geyserPlayerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerStored(UUID uuid) {
        return geyserPlayerConfig.contains("players." + uuid.toString());
    }

    public boolean isPlayerStored(String name) {
        if(getPlayerUUID(name) == null) return false;
        else return true;
    }
}

