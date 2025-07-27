package com.zetaplugins.lifestealz.addon;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class LifeStealZAddon {
    private final AddonMetadata metadata;
    private final JavaPlugin mainPlugin;
    private boolean enabled = false;

    public LifeStealZAddon(AddonMetadata metadata, JavaPlugin mainPlugin) {
        this.metadata = metadata;
        this.mainPlugin = mainPlugin;
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public AddonMetadata getMetadata() {
        return metadata;
    }

    public JavaPlugin getMainPlugin() {
        return mainPlugin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
