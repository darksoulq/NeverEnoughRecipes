<div align="center" style="line-height:0;">
  <img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/banner.png" />
  <br/>
  <a href="https://discord.gg/e35gP423vN"><img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/general_parts/discord_dark.png"/></a><a href="https://jitpack.io/#darksoulq/NeverEnoughRecipes"><img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage@main/plugin_icons/v1/general_parts/builds_dark.png"/></a>
</div>

---

## Features

A recipe viewing system designed to make browsing and understanding items easier, including:

- View all available items in a single menu
- See crafting recipes and usages for any item
- Recents, Inventory, and Favourite modes
- Search menu with filters (@namespace) and soft-matching
- API for adding custom items and recipes to viewer
- Custom recipe layouts with paginated support and search filters for API

And much more.

---

<div align="center">

<table>
<tr>
<td align="center" width="50%">
  <a href="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/main_menu.png">
    <img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/main_menu.png" width="100%" />
  </a><br/>
  <sub><b>Main Menu</b></sub>
</td>
<td align="center" width="50%">
  <a href="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/crafting.png">
    <img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/crafting.png" width="100%" />
  </a><br/>
  <sub><b>Crafting Recipes</b></sub>
</td>
</tr>

<tr>
<td align="center" width="50%">
  <a href="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/furnace.png">
    <img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/furnace.png" width="100%" />
  </a><br/>
  <sub><b>Furnace Recipes</b></sub>
</td>
<td align="center" width="50%">
  <a href="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/smithing.png">
    <img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/smithing.png" width="100%" />
  </a><br/>
  <sub><b>Smithing Recipes</b></sub>
</td>
</tr>
</table>

</div>

---

## Credits

Inspired by:

- [JEI](https://modrinth.com/mod/jei)
- [Polydex](https://modrinth.com/mod/polydex)

# API for other plugins

### Repository
```gradle
maven { url '[https://jitpack.io](https://jitpack.io)' }
```

### Dependency
```gradle
implementation("com.github.darksoulq:NeverEnoughRecipes:<version>")
```
Replace `<version>` with the latest GitHub release.

---

## Creating a Recipe Category

The layout system has been redesigned into `RecipeCategory`.

```java
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YourRecipeCategory extends RecipeCategory<YourRecipeClass> {

    @Override
    public Class<YourRecipeClass> getRecipeClass() {
        return YourRecipeClass.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(YourRecipeClass recipe, ItemStack catalyst) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        
        return new ParsedRecipeView(slotMap, texture, offset, catalyst);
    }

    @Override
    public Set<Integer> getResultSlots() {
        return Set.of(24);
    }

    @Override
    public Set<Integer> getIgnoredSlots() {
        return Set.of();
    }
}
```

### Notes
- **Texture**: Refer to AbyssalLib for loading fonts and `TextureGlyph`s.
- **Offset**: Use `-8` if your texture matches the base texture size.
- **Catalyst**: The provider item (e.g. Crafting Table) is now passed to the parser automatically by the registry.

---

## Registering Content

NER now uses a lifecycle-based plugin registration system. You must implement `NerPlugin` and register your content when the `NerRegistrationEvent` is fired.

### 1. Create your Integration
```java
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.Registration;
import org.bukkit.inventory.ItemStack;

public class MyNerIntegration implements NerPlugin {
    
    @Override
    public void register(Registration registry) {
        
        registry.addCategory(new YourRecipeCategory());
        registry.addCatalyst(YourRecipeClass.class, new ItemStack(Material.CRAFTING_TABLE));
        
        registry.addItem(itemStack);
        registry.addItem("custom_namespace", itemStack); 

        registry.addRecipe(recipeInstance);

        registry.removeRecipe(recipeInstance);
        registry.removeRecipes(recipe -> recipe instanceof YourRecipeClass && shouldRemove((YourRecipeClass) recipe));
        
        registry.addFilter("!", (query, item) -> {
            return item.getType().name().contains(query);
        });

        registry.addDeduplicator(item -> {
            if (item.getType() == Material.POTION) return new ItemStack(Material.POTION);
            return item;
        });

        registry.setNamespaceComparator("custom_namespace", (item1, item2) -> {
            return item1.getType().name().compareTo(item2.getType().name());
        });
    }
}
```

### 2. Hook into NER
Listen for the `NerRegistrationEvent` in your plugin and pass the `Registration` context to your integration class.

```java
import com.github.darksoulq.ner.plugin.NerRegistrationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NerListener implements Listener {

    private final MyNerIntegration integration = new MyNerIntegration();

    @EventHandler
    public void onNerRegistration(NerRegistrationEvent event) {
        integration.register(event.getRegistration());
    }
}
```

Make sure to register your listener in your plugin's `onEnable()`:
```java
@Override
public void onEnable() {
    getServer().getPluginManager().registerEvents(new NerListener(), this);
}
```