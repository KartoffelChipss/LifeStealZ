package org.strassburger.lifestealz.util.data

import org.strassburger.lifestealz.Lifestealz

data class PlayerData (
    var name: String,
    var uuid: String,
    var maxhp: Double = (Lifestealz.instance.config.getInt("startHearts") * 2).toDouble(),
    var craftedHearts: Int = 0,
    var craftedRevives: Int = 0,
    var hasbeenRevived: Int = 0,
    var killedOtherPlayers: Int = 0,
)