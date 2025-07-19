package com.zetaplugins.lifestealz.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {
    public static Map<UUID, Long> lastHeartUse = new HashMap<>();
    public static Map<UUID, Long> lastHeartGain = new HashMap<>();
    public static Map<UUID, Long> lastHeartPickupMessage = new HashMap<>();
}
