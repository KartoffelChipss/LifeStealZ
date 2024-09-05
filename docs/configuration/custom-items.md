---
description: >-
  LifeStealZ introduces a custom heart and revive item, but you can also add
  your own!
---

# 💎 Custom Items

You can find all the custom items under the `items` category in the config file. By default, there are two custom items: `defaultheart` and `revive`. If you want, you can delete `revive`, but **NEVER** delete `defaultheart`, as it is used for /withdraw and other plugin logic.

### Changing item properties

You can change the items properties, as you like. For example the name, lore customModelData and more. Just change the pre existing values.

When changing the mateials for the recipe, please refer to the [Materials](custom-crafting.md) page. Please note, that as of writing this (v1.1.9), there is no way to add hearts to a recipe!

### Creating own custom items

If you want to create your own item, you can just copy the `defaultheart` config and paste it right at the top of the `items` group. That way you are unlikely to mess up the formatting. Remember to change the item ID from `defaultheart` to something unique and either set craftable to false or make the crafting recipe unique! Now you can change all the properties of this item as well.
