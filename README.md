![LifeStealZ Banner](https://file.strassburger.dev/LifeStealZ_banner_new_2.png)

---

![paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/paper_vector.svg)
![purpur](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/purpur_vector.svg)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/github_vector.svg)](https://github.com/KartoffelChipss/lifestealz)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/lifestealz)
[![hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/KartoffelChipss/LifestealZ)
[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://strassburger.org/discord)
[![gitbook](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/gitbook_vector.svg)](https://lsz.strassburger.dev/)
[![generic-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/translate/generic-plural_vector.svg)](https://gitlocalize.com/repo/9581)
[![website](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/website_vector.svg)](https://lifestealz.com)

LifeStealZ is a lifesteal SMP plugin, that allows you to steal hearts from other players, when you kill them. If a player has no more hearts, he is eliminated. You can craft additional hearts or a revive crystal. With this crystal, you can bring back eliminated teammates.

LifeStealZ offers a great amount of admin tools and is highly customizable. You can change every message and change everything about the custom items.

<br>

---

![FeaturesBanner](https://strassburger.org/img/lifestealz/banner_features.png)

**Main Features**

* ✅ Stealing hearts
* ✅ Withdraw hearts
* ✅ Customizable heart items
* ✅ Custom crafting recipes
* ✅ Revive item
* ✅ Maximal and starter hearts
* ✅ Disable totems
* ✅ Disable crystal pvp
* ✅ Ingame recipe viewer
* ✅ PlaceholderAPI placeholders
* ✅ Custom WorldGuard flags
* ✅ Admin commands
* ✅ HEX colors and gradients support
* ✅ SQLite and MySQL support

**Languages**

* English (`en-US`)
* German (`de-DE`)
* Spanish (`es-ES`)
* Czech (`cs-CZ`)
* Polnish (`pl-PL`)
* Vietnamese (`vi-VN`)
* Chinese (Simplified) (`zh-CN`)

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
- **lifestealz.viewhearts** - Allow a player to view the amount of hearts (`/hearts`) he has (true by default)

<br>

---

![ConfigBanner](https://strassburger.org/img/lifestealz/banner_config.png)

The conifuration is split into multiple files located at `plugins/LifeStealZ/`:

<details>
<summary>config.yml</summary>

```yml
#     _      _  __        _____ _             _   ______
#    | |    (_)/ _|      / ____| |           | | |___  /
#    | |     _| |_ ___  | (___ | |_ ___  __ _| |    / /
#    | |    | |  _/ _ \  \___ \| __/ _ \/ _` | |   / /
#    | |____| | ||  __/  ____) | ||  __/ (_| | |  / /__
#    |______|_|_| \___| |_____/ \__\___|\__,_|_| /_____|

# === COLOR CODES ===
# This plugin supports old color codes like: &c, &l, &o, etc.
# It also supports MiniMessage, a more advanced way to format messages:
# https://docs.advntr.dev/minimessage/format.html
# With MiniMessage, you can add HEX colors, gradients, hover and click events, etc.


# === GENERAL SETTINGS ===

# If set to true, LifeStealZ will check for updates and let you know if there's a newer version
checkForUpdates: true

# Set the language to any code found in the "lang" folder (don't add the .yml extension)
# You can add your own language files. Use https://github.com/KartoffelChipss/LifeStealZ/tree/main/src/main/resources/lang/en-US.yml as a template
# If you want to help translating the plugin, please refer to this article: https://lsz.strassburger.dev/contributing/localization
#  | en-US | de-DE | es-ES | fr-FR | cs-CZ | vi-VN | zh-CN | pl-PL | nl-NL | ru-RU
lang: "en-US"


# === WORLD SETTINGS ===

# If set to true, the plugin will only take effect in the worlds listed below
enableWhitelist: false

# A list of worlds, where the plugin should take effect.
# Depending on how your server is arranged, these settings may need to change.
# Ensure the default world on your server is named "world", otherwise rename here!
worlds:
  - "world"
  - "world_nether"
  - "world_the_end"

# Enable to remove the warning message when a world is not whitelisted.
supressWhitelistMessage: false


# === HEART SETTINGS ===

# The amount of hearts a player has, when joining for the first time
startHearts: 10
# The maximal amount of hearts, a player can have
maxHearts: 20
# The amount of hp a player should have after getting reived
reviveHearts: 1
# The amount of hearts the killer should gain and the victim should loose
heartsPerKill: 1
# The amount of hearts a player should loose, when dying naturally
heartsPerNaturalDeath: 1
# The minimal amount of hearts. If a player gets to this amount of hearts, they will be eliminated.
# PLEASE ONLY CHANGE IF YOU KNOW WHAT YOU ARE DOING!
minHearts: 0
# This option will enforce the heart limit on admin commands like /lifestealz hearts <add, set> <player> <amount>
enforceMaxHeartsOnAdminCommands: false
# The custom item that should be dropped if a player is killed (Must be an id from the items.yml)
heartItem: "defaultheart"


# === HEART BEHAVIOR SETTINGS ===

# If hearts should be dropped when killed by player
dropHeartsPlayer: false
# If hearts should be dropped when killed naturally
dropHeartsNatural: true
# If a heart should be dropped, when the killer already has the max amount of hearts
dropHeartsIfMax: true
# If a player should lose a heart, when dying to hostile mobs or falldamage, lava, etc
looseHeartsToNature: true
# If a player should lose a heart, when being killed by another player
looseHeartsToPlayer: true
# Whether it should be announced, when a player got eliminated (has no more hearts)
announceElimination: true

# Allows players to withdraw a heart, even if they only have one left
allowDyingFromWithdraw: true
# If the totem effect should be played, when you use a heart
playTotemEffect: false
# The time you have to wait, before you can use another heart in Milliseconds
heartCooldown: 0
# How many times a player can be revived. Set to -1 to make it infinite
maxRevives: -1


# === Disabling Features ===

# If the use of totems of undying should be prevented
preventTotems: false
# If crystalpvp should be disabled
preventCrystalPVP: false
# If the use of custom items in item frames should be prevented
# It is recommended to leave this enabled, as people can dupe items otherwise
preventCustomItemsInItemFrames: true


# === Extensive Customization ===

# Only disable this option if you want to add custom commands on elimination and don't want the player to get banned
disablePlayerBanOnElimination: false
# If the killer should gain a heart on elimination
heartRewardOnElimination: true

# Execute custom commands on events:
# You can use &player& to insert the player name
# For example: tempban &player& banreason 1d
eliminationCommands:
# - "say &player& got eliminated"
# - "niceCommandtwo"

heartuseCommands:
# - "say &player& used a heart item"

reviveuseCommands:
# - "say &player& revived &target&"

gracePeriod:
  # If a grace period should be enabled
  enabled: false
  # The time in seconds, the grace period should last
  duration: 60
  # If the end of the grace period should be announced
  announce: true
  # If a sound should be played, when the grace period ends
  playSound: true

  # Should a player be able to take damage from players during the grace period
  damageFromPlayers: false
  # Should a player be able to deal damage to players during the grace period
  damageToPlayers: false
  # Should a player be able to use hearts during the grace period
  useHearts: false
  # Should a player be able to loose hearts during the grace period (if set to false, the killer will also not gain a heart)
  looseHearts: false
  # Should a player be able to gain hearts during the grace period
  gainHearts: false

  # Custom commands to be executed when the grace period starts
  startCommands:
    # - "say The grace period for &player& has started"

  # Custom commands to be executed when the grace period ends
  endCommands:
    # - "say The grace period for &player& has ended"

heartGainCooldown:
  # A cooldown for how often people can gain a heart.
  enabled: false
  # How long the cooldown should be in Milliseconds
  cooldown: 120000
  # Drops the heart on the ground if a player kills someone, while still on cooldown
  dropOnCooldown: true
  # Prevents picking up hearts from the groun while on cooldown
  preventPickup: true

antiAlt:
  # If the anti alt system should be enabled
  enabled: true
  # If possible alt kill attempts should be logged
  logAttempt: true
  # If possible alt kill attempts should be prevented
  preventKill: false
  # If a message should be sent to the player, when an alt kill attempt is detected
  sendMessage: false
  # Add custom comamnds, to be executed when a possible alt kill attempt is detected
  # You can use &player& to insert the player name (commands are executed for both players)
  commands:
    # - "say Please don't kill alts"
    # - "ban &player& 1h"

webhook:
  # If a webhook should be sent, when a player is eliminated
  elimination: false
  # If a webhook should be sent, when a player is revived
  revive: false
  # The URL of the webhook
  url: ""
```
</details>

<details>
<summary>storage.yml</summary>

```yml
# === Storage ===

# The type of storage to use. You have the following options:
# "SQLite", "MySQL", "MariaDB"
type: "SQLite"

# This section is only relevant if you use a MySQL database
host: "localhost"
port: 3306
database: "lifestealz"
username: "root"
password: "password"
```
</details>

<details>
<summary>items.yml</summary>

```yml
# === Custom Items ===

# Here you can modify everything about the custom items
# You can change which item is dropped on death in the main config.yml

defaultheart:
  name: "&cHeart"
  lore:
    - "&7Rightclick to use"
  #  - "This would be a second line"
  #  - "And this possibly a third line"
  material: "NETHER_STAR" # Find all materials here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
  enchanted: false
  customModelData: 100
  # Custom item type for the item ("heart", "revive" or "none")
  customItemType: "heart"
  # When customItemType is "heart", this value is used to determine how many hearts the item gives
  customHeartValue: 1
  # The minimum amount of hearts a player must have to use this item (only relevant if customItemType is "heart")
  minHearts: 0
  # The maximum amount of hearts a player can have to use this item (-1 for infinite) (only relevant if customItemType is "heart")
  maxHearts: -1
  # true if this item should be craftable
  craftable: true
  recipe:
    # Every item represents one slot in the crafting table
    # The first item in a row is the left most item in the crafting table
    # If you want a slot to be blank, use 'AIR' or 'empty'
    # If you want to use other custom item (like hearts) use the custom item name (e.g. "defaultheart")
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
  sound:
    enabled: true
    sound: ENTITY_PLAYER_LEVELUP # Find all sounds here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
    volume: 1.0
    pitch: 1.0

revive:
  name: "&dRevive Crystal"
  lore:
    - "&7Rightclick to use"
  material: "AMETHYST_SHARD"
  enchanted: true
  customModelData: 101
  customItemType: "revive"
  customHeartValue: 0
  minHearts: 0
  maxHearts: -1
  craftable: true
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
  sound:
    enabled: false
    sound: ENTITY_PLAYER_LEVELUP
    volume: 1.0
    pitch: 1.0

# You can add as many custom items as you want
```
</details>

If you want a slot in the crafting recipe to be blank, replace the block name with `AIR`.

### WorldGuard Flags

To set a custom worldguard flag, you have to use `/rg flags` and scroll to the last page.

There you can set the following flags:
- **heartloss** - Allow heart loss in this region

<bR>

---

![PlaceholderBanner](https://strassburger.org/img/lifestealz/banner_placeholder.png)

If you are using [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) on your server, you can use the following placeholders:

- **%lifestealz_hearts%** - The amount of hearts a user has
- **%lifestealz_maxhearts%** - The maximum amount of hearts a user can have
- **%lifestealz_health%** - The current health, that the player has (half hearts rounded up)
- **%lifestealz_revived%** - The amount of times a player has been revived
- **%lifestealz_isInGracePeriod%** - If the player is in the grace period
- **%lifestealz_gracePeriodRemaining%** - The remaining time of the grace period
- **%lifestealz_heartCooldown%** - The remaining time of the heart cooldown

<br>

---

![PlaceholderBanner](https://strassburger.org/img/lifestealz/banner_support.png)

If you need help with the setup of the plugin, or found a bug, you can join my discord [here](https://discord.com/invite/Cc76tYwXvy).

[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://strassburger.org/discord)

[Spigot Page](https://www.spigotmc.org/resources/lifestealz.111469/)

---

[![Usage](https://bstats.org/signatures/bukkit/LifeStealZ.svg)](https://bstats.org/plugin/bukkit/LifeStealZ/18735)
