# 📜 Whitelist

**LifestealZ** is designed to be per-world specific, meaning the plugin only takes effect in the worlds you specify. Commands, Lifesteal logic, and items will only function in these designated worlds. This behavior is controlled through LifestealZ's "whitelist," a configuration option for managing which worlds can utilize LifestealZ's features — n_ote that this whitelist is distinct from a server whitelist._

**By default, LifeStealZ** resides over the default Minecraft world names; `world`, `world_nether`, `world_the_end`.

#### Unexpected Behavior

If you receive the message **"This world is not whitelisted for LifeStealZ!"** but do not have Multiverse installed, you may have renamed your world in the panel of your hosting service. Alternatively, depending on your hosting provider, they may also have custom naming for worlds.

In any case, you'll need to customize the worlds that **LifeStealZ** is managing. To do this, read [further](whitelist.md#whitelisting-worlds).

## Whitelisting Worlds

### Finding Your World Name

To correctly assign **LifeStealZ** over a world, you’ll need to have its specific name. If you already know the name of your world you wish to adjust, proceed to the next steps. Otherwise, read along.

You can find the exact name of your world file from the `server.properties` file located on your server. Typically, this is found under **File Manager**, depending on the host.

Navigate down until you find `level-name` (or search for it using `CTRL + F`). Take note of this exact wording, either by copying it or writing it down somewhere.

<details>

<summary>Example Server Properties</summary>

Your Server Properties might look something like this:

<pre class="language-ini"><code class="lang-ini">generator-settings={}
enforce-secure-profile=true
level-name=<a data-footnote-ref href="#user-content-fn-1">mycoolworld</a>
motd=A Minecraft Server
</code></pre>

We are only interested in what proceeds the equals sign (`=`).&#x20;

</details>

Once you know your world name, you can proceed onward by adjusting your **LifeStealZ** config to accommodate these changes.

### Config Changes

With our world name ready, we can simply modify **LifeStealZ's** config. This config file exists within `/plugins/LifeStealZ/config.yml`, and you can access, likewise, through your **File Manager.**&#x20;

Just after the start of the file, you'll find the **Whitelist Settings:**

{% code title="/plugins/LifeStealZ/config.yml" %}
```yaml
worlds:
  - "world"
  - "world_nether"
  - "world_the_end"
```
{% endcode %}

By default, you'll already notice the standard Minecraft world names. From here, just replace all instances of "_world_" with the world name you copied.

<details>

<summary>Example Updated Config</summary>

In our previous example, we copied our world name; for the purpose of our analogy, titled "_mycoolworld_". We can adjust the _config.yml_ to match.

```yaml
worlds:
  - "mycoolworld"
  - "mycoolworld_nether"
  - "mycoolworld_the_end"
```

</details>

Thats it! Just restart your server now, and voila! Everything should work!&#x20;

_For further help, join our_ [_Discord Server_](https://discord.com/invite/Cc76tYwXvy)_._

#### Multiverse Support

LifeStealZ is generally compatible with Multiverse. When switching to a non-whitelisted world, your hearts will be reset to 10. When switching to a whitelisted world, your hearts will be set to your LifeStealZ hearts.

{% hint style="info" %}
**Note:** This functionality is not very reliable and may conflict with other plugins. Therefore, it is recommended to set up multiple servers if you have multiple gamemodes.
{% endhint %}



[^1]: This is the name of our World!&#x20;
