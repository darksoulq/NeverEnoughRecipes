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
maven { url 'https://jitpack.io' }
```

### Dependency
```gradle
implementation("com.github.darksoulq:NeverEnoughRecipes:<version>")
```
Replace `<version>` with the latest GitHub release.

---

## Creating a Recipe Layout

```java
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YourRecipeParser implements RecipeLayout<YourRecipeClass> {

    @Override
    public Class<YourRecipeClass> getRecipeClass() {
        return YourRecipeClass.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(YourRecipeClass recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        // fill slotMap

        return new ParsedRecipeView(slotMap, texture, offset, providerItem);
    }

    @Override
    public Set<Integer> getOutputSlots() {
        // return output slots
        // items in these slots will have this recipe added as a "use"
        return Set.of();
    }
}
```

### Notes
- **Texture**: Refer to AbyssalLib for loading fonts and `TextureGlyph`s
- **Offset**: Use `-8` if your texture matches the base texture size
- **ProviderItem**: The block this recipe belongs to (e.g. crafting table `ItemStack`)

---

## Registering Content

### Add item to namespace
```java
NerApi.addItemToNamespace(itemStack, "namespace");
```
Makes the item visible in the main menu.  
`namespace` can be anything (e.g. your plugin name).

### Register recipe
```java
NerApi.registerRecipe(resultStack, recipe);
```
Assigns a recipe to a result.  
A single recipe can be registered to multiple results.

### Register layout
```java
NerApi.registerLayout(recipeLayout);
```

### Add item without namespace
```java
NerApi.addItem(itemStack);
```

---

## Search Filters

```java
NerApi.registerMenuFilter("prefix", (query, item) -> {
    // return true/false
});
```

---

## Ignoring Vanilla Recipes

```java
NerApi.ignoreVanillaRecipe(namespacedKey);
```

Call this in `onEnable()` if:
- You remove vanilla recipes
- You plan to re-handle them with your own parser  