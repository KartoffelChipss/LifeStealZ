package org.strassburger.lifestealz;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.lifestealz.util.*;
import org.strassburger.lifestealz.util.storage.MariaDBPlayerDataStorage;
import org.strassburger.lifestealz.util.storage.MySQLPlayerDataStorage;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;
import org.strassburger.lifestealz.util.storage.SQLitePlayerDataStorage;
import org.strassburger.lifestealz.util.worldguard.WorldGuardManager;

public final class LifeStealZ extends JavaPlugin {

    static LifeStealZ instance;
    private VersionChecker versionChecker;
    private PlayerDataStorage playerDataStorage;
    private WorldGuardManager worldGuardManager;
    private LanguageManager languageManager;
    private RecipeManager recipeManager;
    private final boolean hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    private final boolean hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    @Override
    public void onLoad() {
        getLogger().info("Loading LifeStealZ...");
        if (hasWorldGuard()) {
            getLogger().info("WorldGuard found! Enabling WorldGuard support...");
            worldGuardManager = new WorldGuardManager();
            getLogger().info("WorldGuard found! Enabled WorldGuard support!");
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        languageManager = new LanguageManager(this);

        playerDataStorage = createPlayerDataStorage();
        playerDataStorage.init();

        recipeManager = new RecipeManager(this);
        recipeManager.registerRecipes();

        versionChecker = new VersionChecker();

        new CommandManager(this).registerCommands();

        new EventManager(this).registerListeners();

        // Register bstats
        int pluginId = 18735;
        new Metrics(this, pluginId);

        if (hasPlaceholderApi()) {
            PapiExpansion papiExpansion = new PapiExpansion();
            if (papiExpansion.canRegister()) {
                papiExpansion.register();
                getLogger().info("PlaceholderAPI found! Enabled PlaceholderAPI support!");
            }
        }

        getLogger().info("LifeStealZ enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LifeStealZ disabled!");
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

    public WorldGuardManager getWorldGuardManager() {
        return worldGuardManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    public boolean hasPlaceholderApi() {
        return hasPlaceholderApi;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    private PlayerDataStorage createPlayerDataStorage() {
        switch (getConfig().getString("storage.type").toLowerCase()) {
            case "mysql":
                getLogger().info("Using MySQL storage");
                return new MySQLPlayerDataStorage();
            case "sqlite":
                getLogger().info("Using SQLite storage");
                return new SQLitePlayerDataStorage();
            case "mariadb":
                getLogger().info("Using MariaDB storage");
                return new MariaDBPlayerDataStorage();
            default:
                getLogger().warning("Invalid storage type in config.yml! Using SQLite storage as fallback.");
                return new SQLitePlayerDataStorage();
        }
    }

    public static void setMaxHealth(OfflinePlayer offlinePlayer, double maxHealth) {
        if (!(offlinePlayer instanceof Player)) return;

        Player player = (Player) offlinePlayer;
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(maxHealth);
        }
    }
}
