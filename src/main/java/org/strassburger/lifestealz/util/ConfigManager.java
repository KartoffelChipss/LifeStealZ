package org.strassburger.lifestealz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.strassburger.lifestealz.LifeStealZ;

import java.io.File;

public final class ConfigManager {
    private final LifeStealZ plugin;

    public ConfigManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getStorageConfig() {
        return getCustomConfig("storage");
    }

    public FileConfiguration getCustomItemConfig() {
        return getCustomConfig("items");
    }

    public FileConfiguration getCustomConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(),  fileName+ ".yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(fileName + ".yml", false);
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }
}
