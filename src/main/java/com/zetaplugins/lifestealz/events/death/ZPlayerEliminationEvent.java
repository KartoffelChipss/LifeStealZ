package com.zetaplugins.lifestealz.events.death;

import com.zetaplugins.lifestealz.events.ZPlayerDeathEventBase;
import com.zetaplugins.lifestealz.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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
    private Component eliminationMessage;

    @Getter @Setter
    private Component kickMessage;

    public ZPlayerEliminationEvent(PlayerDeathEvent originalEvent, Player killer) {
        super(originalEvent);
        this.killer = killer;
        this.isNaturalElimination = killer == null;
        this.shouldBanPlayer = true;
        this.shouldAnnounceElimination = true;
        this.kickMessage = MessageUtils.getAndFormatMsg(
                false,
                "eliminatedJoin",
                "&cYou don't have any hearts left!"
        );
    }
}