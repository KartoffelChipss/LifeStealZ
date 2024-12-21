---
description: This page is about how to use the LifeStealZ API in your own plugin.
icon: code-simple
---

# Using the LifeStealZ API

### Importing LifeStealZ

Use the below code example matching your dependency manager. Replace the version with [the current one](https://github.com/KartoffelChipss/LifeStealZ/releases/latest).

{% tabs %}
{% tab title="Maven" %}
{% code title="pom.xml" %}
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/KartoffelChipss/LifeStealZ</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>org.strassburger</groupId>
        <artifactId>lifestealz</artifactId>
        <version>0.0.0</version>
    </dependency>
</dependencies>
```
{% endcode %}
{% endtab %}

{% tab title="Gradle" %}
```gradle
repositories {
    maven {
        url = 'https://maven.pkg.github.com/KartoffelChipss/LifeStealZ'
    }
}

dependencies {
    compileOnly 'org.strassburger:lifestealz:0.0.0'
}
```
{% endtab %}
{% endtabs %}

### Set LifeStealZ as (soft)depend

In the next step, you will have to go to your `plugin.yml`file and add LifeStealZ as a dependency.

{% tabs %}
{% tab title="Required dependency" %}
{% code title="plugin.yml" %}
```yaml
name: ExamplePlugin
version: 1.0
author: author
main: your.main.path.Here

depend: ["LifeStealZ"]
```
{% endcode %}
{% endtab %}

{% tab title="Optional dependency" %}
{% code title="plugin.yml" %}
```yaml
name: ExamplePlugin
version: 1.0
author: author
main: your.main.path.Here

softdepend: ["LifeStealZ"]
```
{% endcode %}
{% endtab %}
{% endtabs %}

### Use the API methods

To use the API methods, you'll need to get an implementation of the [LifeStealZAPI Interface](https://javadocs.lifestealz.com/org/strassburger/lifestealz/api/LifeStealZAPI.html):&#x20;

```java
package org.strassburger.testPluginMaven;

import org.bukkit.plugin.java.JavaPlugin;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.api.LifeStealZAPI;

public final class TestPluginMaven extends JavaPlugin {

    @Override
    public void onEnable() {
        LifeStealZAPI lifeStealZAPI = LifeStealZ.getAPI();

        getLogger().info("TestPluginMaven enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("TestPluginMaven disabled");
    }
}
```

Read more about the API methods [here](api-methods.md).
