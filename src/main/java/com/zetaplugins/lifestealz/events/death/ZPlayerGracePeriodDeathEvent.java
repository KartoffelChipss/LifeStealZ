package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerGracePeriodDeathEvent extends ZPlayerDeathEventBase {
    @Getter
    private final Player killer; // Can be null
    
    @Getter
    private final boolean isHeartLossBlocked;
    
    @Getter
    private final boolean isHeartGainBlocked;
    
    @Getter @Setter
    private String messageToVictim;
    
    @Getter @Setter
    private String messageToKiller;

    public ZPlayerGracePeriodDeathEvent(PlayerDeathEvent originalEvent, Player killer,
                                        boolean heartLossBlocked, boolean heartGainBlocked) {
        super(originalEvent);
        this.killer = killer;
        this.isHeartLossBlocked = heartLossBlocked;
        this.isHeartGainBlocked = heartGainBlocked;
        this.messageToVictim = "You can't lose hearts during the grace period!";
        this.messageToKiller = killer != null ? "You can't gain hearts during the grace period!" : "";
    }
}
