![LifestealZBanner](https://strassburger.org/img/lifestealz/banner_logo.png)

---

![paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/paper_vector.svg)
![purpur](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/purpur_vector.svg)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/github_vector.svg)](https://github.com/KartoffelChipss/lifestealz)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/lifestealz)

LifeStealZ is a lifesteal SMP plugin, that allows you to steal hearts from other players, when you kill them. If a player has no more hearts, he is eliminated. You can craft additional hearts or a revive crystal. With this crystal, you can bring back eliminated teammates.

LifeStealZ offers a great amount of admin tools and is highly customizable. You can change every message and change everything about the custom items.

<br>

---

![FeaturesBanner](https://strassburger.org/img/lifestealz/banner_features.png)
* ✅ Stealing hearts
* ✅ Withdraw hearts
* ✅ Customizable heart items
* ✅ Custom crafting recipes
* ✅ Revive item
* ✅ Maximal and starter hearts
* ✅ Optional heart loss by natural death
* ✅ Disable totems
* ✅ Disable crystal pvp
* ✅ Ingame recipe viewer
* ✅ PlaceholderAPI placeholders
* ✅ Admin commands

<br>

---

![PermissionsBanner](https://strassburger.org/img/lifestealz/banner_permissions.png)

- **lifestealz.admin.reload** - Allow to reload the plugin
- **lifestealz.admin.setlife** - Allow to set the amount of hearts, a player has
- **lifestealz.admin.giveitem** - Allow to give custom items to a player
- **lifestealz.admin.eliminate** - Allows to eliminate players with the `/eliminate` command
- **lifestealz.admin.revive** - Allow a player to revive another player with the `/revive` command
- **lifestealz.bypassrevivelimit** - Allow a player to bypass the revive limit
- **lifestealz.withdraw** - Allow a player to withdraw hearts (true by default)
- **lifestealz.revive** - Allow a player to revive others with a revive crystal (true by default)
- **lifestealz.viewrecipes** - Allow a player to view the custom recipes (true by default)
- **lifestealz.help** - Allow a player to access the help menu (true by default)

<br>

---

![ConfigBanner](https://strassburger.org/img/lifestealz/banner_config.png)

Here is an example of the configuration file:
<details>
<summary>Click me!</summary>

```yml
#     _      _  __        _____ _             _   ______
#    | |    (_)/ _|      / ____| |           | | |___  /
#    | |     _| |_ ___  | (___ | |_ ___  __ _| |    / /
#    | |    | |  _/ _ \  \___ \| __/ _ \/ _` | |   / /
#    | |____| | ||  __/  ____) | ||  __/ (_| | |  / /__
#    |______|_|_| \___| |_____/ \__\___|\__,_|_| /_____|

#A list of worlds, where the plugin should take effect
worlds:
  - "world"
  - "world_nether"
  - "world_the_end"

#The amount of hearts a player has, when joining for the first time
startHearts: 10
#The maximal amount of hearts, a player can have
maxHearts: 20
# This option will enforce the heart limit on admin commands like /lifestealz hearts <add, set> <player> <amount>
enforceMaxHeartsOnAdminCommands: false

#If a heart should be dropped, when the killer already has the max amount of hearts
dropHeartsIfMax: true
#If a player should lose a heart, when dying to hostile mobs or falldamage, lava, etc
looseHeartsToNature: true
#If a player should lose a heart, when being killed by another player
looseHeartsToPlayer: true
#Whether it should be announced, when a player got eliminated (has no more hearts)
announceElimination: true

#Allows to craft hearts
allowHeartCrafting: true
#Allows players to withdraw a heart, even if they only have one left
allowDyingFromWithdraw: true
#If the totem effect should be played, when you use a heart
playTotemEffect: false

#How many times a player can be revived. Set to -1 to make it infinite
maxRevives: -1
#Allows to craft revive crystal
allowReviveCrafting: true

#If the use of totems of undying should be prevented
preventTotems: false
#If crystalpvp should be disabled
preventCrystalPVP: false

#Only disable this option if you want to add custom commands on elimination and don't want the player to get banned
disablePlayerBanOnElimination: false
# The amount of hp a player should have after getting eliminated
respawnHP: 10
# The command that should be executed when a player gets eliminated
# You can use &player& to insert the player name
# For example: tempban &player& banreason 1d
eliminationCommand: say &player& got eliminated

#Here you can modify everything about the custom items
items:
  heart:
    name: "&cHeart"
    lore:
      - "&7Rightclick to use"
    #     - "This would be a second line"
    #     - "And this possibly a third line"
    material: "NETHER_STAR"
    enchanted: false
    customModelData: 100
    recipe:
      #Every item represents one slot in the crafting table
      #The first item in a row is the left most item in the crafting table
      #If you want a slot to be blant, use 'AIR'
      rowOne:
        - "GOLD_BLOCK"
        - "GOLD_BLOCK"
        - "GOLD_BLOCK"
      rowTwo:
        - "OBSIDIAN"
        - "NETHER_STAR"
        - "OBSIDIAN"
      rowThree:
        - "DIAMOND_BLOCK"
        - "DIAMOND_BLOCK"
        - "DIAMOND_BLOCK"

  revive:
    name: "&dRevive Crystal"
    lore:
      - "&7Rightclick to use"
    material: "AMETHYST_SHARD"
    enchanted: true
    customModelData: 101
    recipe:
      rowOne:
        - "AMETHYST_SHARD"
        - "NETHERITE_BLOCK"
        - "AMETHYST_SHARD"
      rowTwo:
        - "OBSIDIAN"
        - "BEACON"
        - "OBSIDIAN"
      rowThree:
        - "AMETHYST_SHARD"
        - "NETHERITE_BLOCK"
        - "AMETHYST_SHARD"

#You can modify all messages here
messages:
  prefix: "&8[&cLifeStealZ&8]"
  usageError: "&cUsage: %usage%"
  noPermissionError: "&cYou don't have permission to use this!"
  noPlayerData: "&cThis player has not played on this server yet!"
  eliminateSuccess: "&7You successfully eliminated &c%player%&7!"
  reviveSuccess: "&7You successfully revived &c%player%!"
  reviveMaxReached: "&cThis player has already been revived %amount% times!"
  onlyReviveElimPlayers: "&cYou can only revive eliminated players!"
  eliminatedJoin: "&cYou don't have any hearts left!"
  eliminationAnnouncement: "&c%player% &7has been eliminated by &c%killer%&7!"
  eliminateionAnnouncementNature: "&c%player% &7has been eliminated!"
  setHeartsConfirm: "&7Successfully set &c%player%&7's hearts to &c%amount%"
  getHearts: "&c%player% &7currently has &c%amount% &7hearts!"
  reloadMsg: "&7Successfully reloaded the plugin!"
  versionMsg: "&7You are using version %version%"
  noWithdraw: "&cYou would be eliminated, if you withdraw a heart!"
  withdrawConfirmmsg: "&8&oUse /withdrawheart confirm if you really want to withdraw a heart"
  maxHeartLimitReached: "&cYou already reached the limit of %limit% hearts!"
  closeBtn: "&cClose"
  reviveTitle: "&8Revive a player"
  revivePlayerDesc: "&7Click to revive this player"
```
</details>

If you want a slot in the crafting recipe to be blank, replace the block name with `AIR`.

<bR>

---

![PlaceholderBanner](https://strassburger.org/img/lifestealz/banner_placeholder.png)

If you are using [Placeholderapi](https://www.spigotmc.org/resources/placeholderapi.6245/) on your server, you can use the following placeholders:

- **%lifestealz_hearts%** - The amount of hearts a user has
- **%lifestealz_maxhearts%** - The maximum amount of hearts a user can have
- **%lifestealz_revived%** - The amount of times a player has been revived
- **%lifestealz_craftedhearts%** - The amount of times a player has crafted a heart
- **%lifestealz_craftedrevives%** - The amount of times a player has crafted a revive crystal

<br>

---

![PlaceholderBanner](https://strassburger.org/img/lifestealz/banner_support.png)

If you need help with the setup of the plugin, or found a bug, you can join my discord [here](https://discord.com/invite/Cc76tYwXvy) or message me directly (Kartoffelchips#0445).

[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://strassburger.org/discord)

---

[![Usage](https://bstats.org/signatures/bukkit/LifeStealZ.svg)](https://bstats.org/plugin/bukkit/LifeStealZ/18735)
