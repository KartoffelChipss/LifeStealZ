package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerHeartGainCooldownEvent extends ZPlayerDeathEventBase {
    @Getter
    private final Player killer;
    
    @Getter
    private final long timeLeftOnCooldown;
    
    @Getter @Setter
    private boolean shouldDropHeartsInstead;
    
    @Getter @Setter
    private String cooldownMessage;

    public ZPlayerHeartGainCooldownEvent(PlayerDeathEvent originalEvent, Player killer, long timeLeft) {
        super(originalEvent);
        this.killer = killer;
        this.timeLeftOnCooldown = timeLeft;
        this.shouldDropHeartsInstead = false;
        this.cooldownMessage = "You have to wait before gaining another heart!";
    }
}
