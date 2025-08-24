package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import com.zetaplugins.lifestealz.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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
    private Component messageToVictim;
    
    @Getter @Setter
    private Component messageToKiller;

    public ZPlayerGracePeriodDeathEvent(PlayerDeathEvent originalEvent, Player killer,
                                        boolean heartLossBlocked, boolean heartGainBlocked) {
        super(originalEvent);
        this.killer = killer;
        this.isHeartLossBlocked = heartLossBlocked;
        this.isHeartGainBlocked = heartGainBlocked;
        this.messageToVictim = MessageUtils.getAndFormatMsg(
                false,
                "noHeartLossInGracePeriod",
                "&cYou can't lose hearts during the grace period!"
        );
        this.messageToKiller = killer != null ? MessageUtils.getAndFormatMsg(
                false,
                "noHeartGainInGracePeriod",
                "&cYou can't gain hearts during the grace period!") : Component.text("");
    }
}
