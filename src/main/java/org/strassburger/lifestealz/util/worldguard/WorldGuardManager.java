package org.strassburger.lifestealz.util.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;

public class WorldGuardManager {
    private StateFlag HEARTLOSS_FLAG;

    public WorldGuardManager() {
        registerFlags();
    }

    private void registerFlags() {
        StateFlag heartLossFlag = new HeartLossFlag();
        WorldGuard.getInstance().getFlagRegistry().register(heartLossFlag);
        HEARTLOSS_FLAG = heartLossFlag;
    }

    public StateFlag getHeartLossFlag() {
        return HEARTLOSS_FLAG;
    }

    public static boolean checkHeartLossFlag(Player player) {
        com.sk89q.worldguard.LocalPlayer localPlayer = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location loc = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.getLocation());
        com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.regions.RegionQuery query = container.createQuery();

        com.sk89q.worldguard.protection.ApplicableRegionSet set = query.getApplicableRegions(loc);

        return set.testState(localPlayer, LifeStealZ.getInstance().getWorldGuardManager().getHeartLossFlag());
    }
}
