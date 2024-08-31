package org.strassburger.lifestealz;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.lifestealz.util.*;
import org.strassburger.lifestealz.util.storage.MariaDBStorage;
import org.strassburger.lifestealz.util.storage.MySQLStorage;
import org.strassburger.lifestealz.util.storage.Storage;
import org.strassburger.lifestealz.util.storage.SQLiteStorage;
import org.strassburger.lifestealz.util.worldguard.WorldGuardManager;

public final class LifeStealZ extends JavaPlugin {

    static LifeStealZ instance;
    private VersionChecker versionChecker;
    private Storage storage;
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

        storage = createPlayerDataStorage();
        storage.init();

        recipeManager = new RecipeManager(this);
        recipeManager.registerRecipes();

        versionChecker = new VersionChecker();

        new CommandManager(this).registerCommands();

        new EventManager(this).registerListeners();

        initializeBStats();

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

    public Storage getStorage() {
        return storage;
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

    private Storage createPlayerDataStorage() {
        switch (getConfig().getString("storage.type").toLowerCase()) {
            case "mysql":
                getLogger().info("Using MySQL storage");
                return new MySQLStorage(this);
            case "sqlite":
                getLogger().info("Using SQLite storage");
                return new SQLiteStorage(this);
            case "mariadb":
                getLogger().info("Using MariaDB storage");
                return new MariaDBStorage(this);
            default:
                getLogger().warning("Invalid storage type in config.yml! Using SQLite storage as fallback.");
                return new SQLiteStorage(this);
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

    private void initializeBStats() {
        int pluginId = 18735;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("storage_type", () -> getConfig().getString("storage.type")));
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> getConfig().getString("lang")));
    }
}
