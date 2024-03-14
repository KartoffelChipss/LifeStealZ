package org.strassburger.lifestealz.util;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.strassburger.lifestealz.Lifestealz;
import org.strassburger.lifestealz.util.worldguardflags.HeartLossFlag;

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
}
