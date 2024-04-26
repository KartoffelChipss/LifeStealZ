package org.strassburger.lifestealz.util;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {
    public static Map<UUID, Inventory> REVIVE_GUI_MAP = new HashMap<>();
    public static Map<UUID, Inventory> RECIPE_GUI_MAP = new HashMap<>();
}
