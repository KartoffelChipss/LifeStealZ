---
description: The main command for LifeStealZ
---

# 👨‍💻 /lifestealz

Command `/lifestealz` (`/lsz`)

## Usage

### /lifestealz help

Permission: `lifestealz.help` (default: true)

&#x20;This command prints a help message.

### /lifestealz recipe \[recipe]

Permission: `lifestealz.viewrecipes` (default: true)

Shows a GUI with the recipe for a specific item.

### /lifestealz reload

Permission: `lifestealz.admin.reload` (default: op)

This command reloads the plugin, to load changes in the config.

### /lifestealz hearts \<add | set | remove | get> \[player] \[amount]

Permission: `lifestealz.admin.setlife` (default: op)

This command sets, adds, removes, or gets the hearts of the targetplayer. The amount must not be specified, when using get.

### /lifestealz giveItem \[player] \[item] \[amount] \<silent>

Permission: `lifestealz.admin.giveitem` (default: op)

This command gives a custom item (e.g. heart or revive crystal) to a player. The `<item>` can be any item specified in the config. The amount does not have to be specified and defaults to 1. If you don't want a confirmation message to be sent to the targetplayer, you can add `silent` at the end of the command.
