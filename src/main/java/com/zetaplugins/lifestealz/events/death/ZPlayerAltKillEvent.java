package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerAltKillEvent extends ZPlayerDeathEventBase {
    @Getter
    private final Player suspectedAlt;
    
    @Getter
    private final String sharedIP;
    
    @Getter @Setter
    private boolean shouldPreventKill;
    
    @Getter @Setter
    private boolean shouldLogAttempt;
    
    @Getter @Setter
    private boolean shouldSendMessage;
    
    @Getter @Setter
    private String warningMessage;

    public ZPlayerAltKillEvent(PlayerDeathEvent originalEvent, Player suspectedAlt, String sharedIP) {
        super(originalEvent);
        this.suspectedAlt = suspectedAlt;
        this.sharedIP = sharedIP;
        this.shouldPreventKill = true;
        this.shouldLogAttempt = true;
        this.shouldSendMessage = true;
        this.warningMessage = "Please don't kill alts! This attempt has been logged!";
    }
}