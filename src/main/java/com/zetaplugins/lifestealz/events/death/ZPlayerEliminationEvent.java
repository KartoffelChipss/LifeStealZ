package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZPlayerEliminationEvent extends ZPlayerDeathEventBase {
    @Getter
    private final Player killer; // Can be null for natural elimination
    
    @Getter
    private final boolean isNaturalElimination;
    
    @Getter @Setter
    private boolean shouldBanPlayer;
    
    @Getter @Setter
    private boolean shouldAnnounceElimination;
    
    @Getter @Setter
    private String eliminationMessage;
    
    @Getter @Setter
    private String kickMessage;

    public ZPlayerEliminationEvent(PlayerDeathEvent originalEvent, Player killer) {
        super(originalEvent);
        this.killer = killer;
        this.isNaturalElimination = killer == null;
        this.shouldBanPlayer = true;
        this.shouldAnnounceElimination = true;
        this.eliminationMessage = "";
        this.kickMessage = "You don't have any hearts left!";
    }
}