package org.strassburger.lifestealz;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.lifestealz.api.LifeStealZAPI;
import org.strassburger.lifestealz.api.LifeStealZAPIImpl;
import org.strassburger.lifestealz.util.*;
import org.strassburger.lifestealz.caches.EliminatedPlayersCache;
import org.strassburger.lifestealz.caches.OfflinePlayerCache;
import org.strassburger.lifestealz.util.commands.CommandManager;
import org.strassburger.lifestealz.util.customitems.recipe.RecipeManager;
import org.strassburger.lifestealz.util.geysermc.GeyserManager;
import org.strassburger.lifestealz.util.geysermc.GeyserPlayerFile;
import org.strassburger.lifestealz.storage.MariaDBStorage;
import org.strassburger.lifestealz.storage.MySQLStorage;
import org.strassburger.lifestealz.storage.Storage;
import org.strassburger.lifestealz.storage.SQLiteStorage;
import org.strassburger.lifestealz.util.worldguard.WorldGuardManager;

import java.io.File;

public final class LifeStealZ extends JavaPlugin {

    private VersionChecker versionChecker;
    private Storage storage;
    private WorldGuardManager worldGuardManager;
    private LanguageManager languageManager;
    private ConfigManager configManager;
    private RecipeManager recipeManager;
    private GeyserManager geyserManager;
    private GeyserPlayerFile geyserPlayerFile;
    private WebHookManager webHookManager;
    private GracePeriodManager gracePeriodManager;
    private EliminatedPlayersCache eliminatedPlayersCache;
    private OfflinePlayerCache offlinePlayerCache;
    private AsyncTaskManager asyncTaskManager;
    private final boolean hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    private final boolean hasPlaceholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    private final boolean hasGeyser = Bukkit.getPluginManager().getPlugin("Geyser-Spigot") != null;


    @Override
    public void onLoad() {
        getLogger().info("Loading LifeStealZ...");

        if (Bukkit.getName().toLowerCase().contains("spigot") || Bukkit.getName().toLowerCase().contains("craftbukkit")) {
            getLogger().severe("---------------------------------------------------");
            getLogger().severe("LifeStealZ does not support Spigot or Bukkit!");
            getLogger().severe("Please use Paper or any fork of Paper (like Purpur). If you need further assistance, please join our Discord server:");
            getLogger().severe("https://dc.lifestealz.com/");
            getLogger().severe("---------------------------------------------------");
        }

        if (hasWorldGuard()) {
            getLogger().info("WorldGuard found! Enabling WorldGuard support...");
            worldGuardManager = new WorldGuardManager();
            getLogger().info("WorldGuard found! Enabled WorldGuard support!");
        }
    }

    @Override
    public void onEnable() {
        if (hasGeyser()) {
            getLogger().info("Geyser found, enabling Bedrock player support.");
            geyserPlayerFile = new GeyserPlayerFile();
            geyserManager = new GeyserManager();
        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        asyncTaskManager = new AsyncTaskManager();

        languageManager = new LanguageManager(this);
        configManager = new ConfigManager(this);

        storage = createPlayerDataStorage();
        storage.init();

        recipeManager = new RecipeManager(this);
        recipeManager.registerRecipes();

        versionChecker = new VersionChecker(this, "l8Uv7FzS");
        gracePeriodManager = new GracePeriodManager(this);
        webHookManager = new WebHookManager(this);

        eliminatedPlayersCache = new EliminatedPlayersCache(this);
        offlinePlayerCache = new OfflinePlayerCache(this);

        new CommandManager(this).registerCommands();

        new EventManager(this).registerListeners();

        initializeBStats();

        if (hasPlaceholderApi()) {
            PapiExpansion papiExpansion = new PapiExpansion(this);
            if (papiExpansion.canRegister()) {
                papiExpansion.register();
                getLogger().info("PlaceholderAPI found! Enabled PlaceholderAPI support!");
            }
        }

        getLogger().info("LifeStealZ enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Canceling all running tasks...");
        asyncTaskManager.cancelAllTasks();
        getLogger().info("LifeStealZ disabled!");
    }

    public static LifeStealZ getInstance() {
        return JavaPlugin.getPlugin(LifeStealZ.class);
    }

    public static LifeStealZAPI getAPI() {
        return new LifeStealZAPIImpl(getInstance());
    }

    public AsyncTaskManager getAsyncTaskManager() {
        return asyncTaskManager;
    }

    public VersionChecker getVersionChecker() {
        return versionChecker;
    }

    public Storage getStorage() {
        return storage;
    }

    public EliminatedPlayersCache getEliminatedPlayersCache() {
        return eliminatedPlayersCache;
    }

    public OfflinePlayerCache getOfflinePlayerCache() {
        return offlinePlayerCache;
    }

    public WorldGuardManager getWorldGuardManager() {
        return worldGuardManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public GracePeriodManager getGracePeriodManager() {
        return gracePeriodManager;
    }

    public GeyserManager getGeyserManager() {
        return geyserManager;
    }

    public GeyserPlayerFile getGeyserPlayerFile() {
        return geyserPlayerFile;
    }

    public boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    public boolean hasPlaceholderApi() {
        return hasPlaceholderApi;
    }

    public boolean hasGeyser() {
        return hasGeyser;
    }

    public WebHookManager getWebHookManager() {
        return webHookManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private Storage createPlayerDataStorage() {
        switch (getConfigManager().getStorageConfig().getString("type").toLowerCase()) {
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

    public static void setMaxHealth(Player player, double maxHealth) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(maxHealth);
        }
    }

    private void initializeBStats() {
        int pluginId = 18735;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("storage_type", () -> getConfigManager().getStorageConfig().getString("type")));
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> getConfig().getString("lang")));
    }

    public File getPluginFile() {
        return this.getFile();
    }
}
