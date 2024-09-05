---
description: Withdraw hearts into items.
---

# 📤 /withdrawheart

## Usage

### /withdrawheart \[amount] \<confirm>

Permission: `lifestealz.withdraw` (default: true)

Withdraw a heart. You will loose a max heart, but get a heart item (The `defaultheart` custom item from the config). You can specify an amount, to withdraw multiple hearts at once.

If `allowDyingFromWithdraw` is enabled in the config, you can add `confirm` at the end of the command to withdraw hearts even if you would be eliminated by doing so.
