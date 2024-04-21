package org.strassburger.lifestealz;

import org.bstats.bukkit.Metrics;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.lifestealz.util.CommandManager;
import org.strassburger.lifestealz.util.EventManager;
import org.strassburger.lifestealz.util.RecipeManager;
import org.strassburger.lifestealz.util.VersionChecker;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;
import org.strassburger.lifestealz.util.storage.SQLitePlayerDataStorage;

import java.util.Map;
import java.util.UUID;

public final class LifeStealZ extends JavaPlugin {

    static LifeStealZ instance;
    private VersionChecker versionChecker;
    private PlayerDataStorage playerDataStorage;

    public static Map<UUID, Inventory> recipeGuiMap;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        CommandManager.registerCommands();

        EventManager.registerListeners();

        RecipeManager.registerRecipes();

        playerDataStorage = createPlayerDataStorage();
        playerDataStorage.init();

        versionChecker = new VersionChecker();

        // Register bstats
        int pluginId = 18735;
        new Metrics(this, pluginId);

        getLogger().info("TemplatePlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TemplatePlugin disabled!");
    }

    public static LifeStealZ getInstance() {
        return instance;
    }

    public VersionChecker getVersionChecker() {
        return versionChecker;
    }

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    private PlayerDataStorage createPlayerDataStorage() {
        String option = getConfig().getString("storage.type");

        if (option.equalsIgnoreCase("mysql")) {
            // Todo("Implement MySQL storage");
        }

        getLogger().info("Using SQLite storage");
        return new SQLitePlayerDataStorage();
    }

    public static void setMaxHealth(Player player, double maxHealth) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(maxHealth);
            player.setHealthScale(maxHealth);
        }
    }
}
