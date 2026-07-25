# Registry and Integrations
<link-summary>Learn how to hook into NER to add custom items, recipes, item groups, search filters, and craftability checkers.</link-summary>

NER uses a lifecycle-based registration system. Instead of registering objects sporadically across your plugin, you define an integration class that implements `NerPlugin`. When NER compiles its registries, it will call your integration and pass a `Registration` instance.

### 1. Creating Your Integration Class

Create a new class and implement `NerPlugin`. The `register` method exposes the API to inject custom content into the recipe browser.

<code-block lang="java">
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.Registration;
import com.github.darksoulq.ner.util.CraftabilityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MyNerIntegration implements NerPlugin {

    @Override
    public void register(Registration registry) {
        
        // Registering custom items to appear in the Main Menu
        registry.addItem(new ItemStack(Material.ECHO_SHARD)); 
        registry.addItem("my_custom_addon", customItemStack); 

        // Grouping items visually in the Main Menu
        registry.addItemGroup("custom_swords", Component.text("Custom Swords"), listOfSwords, true);

        // Registering or removing specific recipes
        registry.addRecipe(myCustomRecipeInstance);
        registry.removeRecipes(recipe -> recipe instanceof MyRecipeClass &amp;&amp; shouldRemove((MyRecipeClass) recipe));
        
        // Register custom search prefixes
        registry.addFilter("!", (query, item) -> item.getType().name().toLowerCase().contains(query));

        // Deduplicators merge item variations into a single display item
        registry.addDeduplicator(item -> {
            if (item.getType() == Material.SUSPICIOUS_STEW) return new ItemStack(Material.SUSPICIOUS_STEW);
            return item;
        });

        // Modifiers change how an item looks specifically for the player viewing it
        registry.addModifier((player, item) -> {
            if (shouldObscure(item)) applyObfuscation(item);
            return item;
        });

        // Registering logic to check if a player has the ingredients for a recipe
        registry.addCraftabilityChecker(MyRecipeClass.class, (player, recipe) -> 
            CraftabilityUtil.hasIngredients(player, recipe.getChoices())
        );

        // Custom sorting logic for your specific namespace
        registry.setNamespaceComparator("my_custom_addon", (item1, item2) -> 
            item1.fold(i -> i.getType().name(), g -> g.id())
                 .compareTo(item2.fold(i -> i.getType().name(), g -> g.id()))
        );
    }
}
</code-block>

### 2. Understanding the Registry Methods

Here is a breakdown of the core registry mechanics available to you:

#### Search Filters (`addFilter`)
Adds custom search prefixes for the Anvil search menu. NER includes the following default filters:
* `@` — Searches by **Namespace** (e.g., `@minecraft`).
* `:` — Searches inside the item's **Lore**.
* `#` — Searches by the item's **Tags** (e.g., `#logs`).
* `$` — Searches by the **Crafting Station** or provider (e.g., `$furnace` finds items smelted or used in a furnace).

#### Item Groups (`addItemGroup`)
Groups a `List<ItemStack>` under a single collapsible header in the main menu. Setting `animate` to `true` will cycle through the items in the group icon dynamically.

#### Craftability Checkers (`addCraftabilityChecker`)
When a player filters the main menu by "Craftable", NER needs to know how to verify if a player has the right items. You bind your custom recipe class to a checker logic here. `CraftabilityUtil.hasIngredients()` safely simulates taking items from a cached player inventory.

#### Deduplicators (`addDeduplicator`)
Useful when you have hundreds of NBT variations of the same item that share the exact same recipe (like potions or suspicious stews). It forces the indexer to treat them as a single generic item in the GUI to prevent clutter.

#### Modifiers (`addModifier`)
Useful if items should display differently depending on the player's permissions or progression. The modifier applies *only* visually when rendering the GUI button.

### 3. Hooking into NER

To finalize the setup, register your integration class during your plugin's `onEnable()` phase. Because you set `load: BEFORE` for NER in your `paper-plugin.yml`, NER is guaranteed to be ready.

<code-block lang="java">
import com.github.darksoulq.ner.NeverEnoughRecipes;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Hook into NER
        NeverEnoughRecipes.registerPlugin(new MyNerIntegration());
    }
}
</code-block>

<tip>
If a server admin uses <code>/ner reload</code>, NER clears all caches and re-triggers your <code>register()</code> method automatically. You do not need to manually listen for reloads.
</tip>