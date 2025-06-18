---
description: >-
  LifeStealZ introduces a custom heart and revive item, but you can add as many
  custom items as you wish!
---

# 💎 Custom Items

{% hint style="danger" %}
If you want to delete the custom item with the ID `defaultheart`, you will have to change the `heartItem` settings in the `config.yml` file to another item ID.
{% endhint %}

You can find all the custom items in the `items.yml` file. Here you can add as many items as you want. Each item follows this pattern.&#x20;

If you remove an item setting, the default value will be applied.

```yaml
itemId: # <- This is the item id that can be used in recipes and for permissions
  name: "&cHeart"
  lore:
    - "&7Rightclick to use"
    - "This would be a second line"
  material: "NETHER_STAR"
  enchanted: false
  customItemType: "heart"
  # --- Heart Item Settings --- (only relevant if customItemType is "heart")
  customHeartValue: 1
  minHearts: 0
  maxHearts: -1
  # --- End of Heart Item Settings ---
  # --- Revive Beacon Settings --- (only relevant if customItemType is "revivebeacon")
  reviveTime: 30
  allowBreakingBeaconWhileReviving: true
  decoyMaterial: "RED_STAINED_GLASS"
  showEnchantParticles: true
  showLaser: true
  innerLaserMaterial: "RED_GLAZED_TERRACOTTA"
  outerLaserMaterial: "RED_STAINED_GLASS"
  showParticleRing: true
  particleColor: "RED"
  # --- End of Revive Beacon Settings ---
  requirePermission: false
  craftable: true
  recipes:
    1:
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
  invulnerable: false
  despawnable: true
  sound:
    enabled: true
    sound: ENTITY_PLAYER_LEVELUP
    volume: 1.0
    pitch: 1.0
```

### General item properties:

* Replace `itemId` with an id for your item (only letters recommended), that will be used for permissions and commands.
* `name` refers to the name of the item ([Formatting rules](messages.md#formatting) apply here)
* `lore` is a list lines, that will be displayed as the item description. You can use `" "` for an empty line and the general [formatting rules](messages.md#formatting). You can add as many lines as you want.
* `material` refers to the type of the item (e.g. Nether Star, Stick, Diamond Pickaxe). You can use any material from [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html).
* `enchanted`: Set this to `true` if you item should have an enchant glint.
* If `invulnerable` is set to `true`, the item will not be destroyed by fire lava, explosions, cacti etc.
* If `despawnable` is set to `false`, the item will not despawn after laying on the floor for more than 5 minutes.

### Custom Textures

Every item in LifeStealZ has a `minecraft:custom_model_data` property of `lifestealz_itemId`.

If you don't want to make your own texturepack, you can use the [Official LifeStealZ Resourcepack](https://modrinth.com/resourcepack/lifestealzpack) or use it as a template for your own.

{% embed url="https://modrinth.com/resourcepack/lifestealzpack" %}
Official LifeStealZ Resourcepack on Modrinth
{% endembed %}

### Crafting

If you want your item to be craftable in a crafting table, you can set `craftable` to `true`.

You can add as many recipes as you want, by adding another recipe id (like the `1`in the example) with three rows each.

Every recipe has a `rowOne`, `rowTwo` and `rowThree` representing the first three rows in the crafting table. Each row has a list of three [Materials](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html), that represent the three items in that crafting table row. Instead of a Material, you can also use:

* `AIR`or `empty`for an empty slot in the crafting grid
* the id of another custom item (e.g. `defaultheart`)&#x20;
* block or item tags by putting a `#` in front of the tag (e.g. `#logs` or `#wool`)

{% tabs %}
{% tab title="Recipe" %}
```yaml
recipes:
    1:
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
```
{% endtab %}

{% tab title="Crafting Table" %}
<figure><img src="../.gitbook/assets/crafting-grid(1).png" alt=""><figcaption></figcaption></figure>
{% endtab %}
{% endtabs %}

### Custom Item Behavior

* `requirePermission`: If this is set to true, players will need to have the `lifestealz.item.itemid` permission, where `itemid` is the ID of your item.
* `customItemType` can be either `none`, `heart` or `revive`.
  * `none`: This item will have no special functionality and can be used in other LifeStealZ crafting recipes for example.
  * `heart`: This item will grant the user one or multiple hearts (see further config options in this category)
  * `revive`: This will make the item act as revive item. Players can right click with this item to instantly revive eliminated players.
  * `revivebeacon`: This will make the item a revive beacon. Revive beacons can be placed down to revive a player. You can configure how long it takes to revive a player and if the process can be canceled by destorying the beacon.

### Heart Settings

These settings are only relevant if `customItemType` is set to `heart`.

* `customHeartValue`: Here you can set how many hearts a user should get when using this item
* `minHearts`: Here you can set the minimum amount of hearts a user needs to already have to be able to use this item.
* `maxHearts`: Here you can set the maximum amount of hearts a user can have to be able to use this item. Setting it to -1 will disable this.

### Revive Beacon Settings

These settings are only relevant if `customItemType` is set to `revivebeacon`.

* `reviveTime`: The time in seconds it takes to revive a player.
* `allowBreakingBeaconWhileReviving`: Wether or not you can interrupt the revive process by destroying the revive beacon.
* `decoyMaterial`: The [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html), that should be around the beacon block.
* `showEnchantParticles`: Whether or not enchant particles should spawn around the revive beacon.
* `showLaser`: Wether or not the revive beacon should fire a laser in the air while reviving a player.
* `innerLaserMaterial`: The [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) of the inner part of the laser.
* `outerLaserMaterial`: The [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) of the outer part of the laser. (Recommended to be a semi-transparent material)
* `showParticleRing`: Wether or not to show a particle ring around the revive beacon while reviving a player.
* `particleColor`: The color of the particle ring. Possible values: `WHITE`, `GRAY`, `RED`, `ORANGE`, `YELLOW`, `GREEN`, `BLUE`, `PURPLE`, `PINK`

### Custom Sound

If you want your to customize the sound your heart item makes when consuming it, you can refer to the `sound` section. You can use any sound from [this list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html) as well as setting the volume and pitch. If you want, you can also disable the sound completely.
