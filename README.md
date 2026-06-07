<div align="center" style="line-height:0;">
  <img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/ner/banner.png" />
  <br/>
  <a href="https://discord.gg/e35gP423vN"><img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage/plugin_icons/v1/general_parts/discord_dark.png"/></a><a href="https://jitpack.io/#darksoulq/NeverEnoughRecipes"><img src="https://cdn.jsdelivr.net/gh/darksoulq/ImageStorage@main/plugin_icons/v1/general_parts/builds_dark.png"/></a>
</div>

---

## Requirements:
The plugin requires [AbyssalLib](https://modrinth.com/plugin/abyssallib)

## Features

A recipe viewing system designed to make browsing and understanding items easier, including:

- View all available items in a single menu
- See crafting recipes and usages for any item
- Recents, Inventory, and Favourite modes
- Search menu with filters (`@namespace`, `:lore`, `$crafting station`) and soft-matching
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

## Creating a Recipe Category

The layout system has been redesigned into `RecipeCategory`. You can now easily define static slots, recipe choices, probabilities, paginated sections (mini-pages), and sequential progression stages via the `ParsedRecipeView.Builder` and `RecipeStage.Builder`.

```java
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.RecipeStage;
import com.github.darksoulq.ner.model.PagedSection;
import com.github.darksoulq.ner.model.SectionButton;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Set;

public class YourRecipeCategory extends RecipeCategory<YourRecipeClass> {

    @Override
    public Class<YourRecipeClass> getRecipeClass() {
        return YourRecipeClass.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(YourRecipeClass recipe, ItemStack catalyst) {
        ParsedRecipeView.Builder builder = ParsedRecipeView.builder(baseTexture, baseOffset, catalyst)
            .set(4, recipe.getInput())
            .setChoice(5, recipe.getIngredientChoice())
            .probability(recipe.getPrimaryOutput(), "50%")
            .probability(recipe.getSecondaryOutput(), 12.5f)
            .addSection(new PagedSection(
                new int[]{10, 11, 12, 13}, 
                recipe.getOutputs(), 
                new SectionButton(9, previousIconStack), 
                new SectionButton(14, nextIconStack)     
            ));

        for (int i = 0; i < recipe.getExtraStepsCount(); i++) {
            builder.addStage(RecipeStage.builder(stepTexture, stepOffset)
                .set(20, recipe.getStepInput(i))
                .set(24, recipe.getStepOutput(i))
                .build());
        }

        return builder.build();
    }

    @Override
    public Set<Integer> getResultSlots() {
        return Set.of(24, 10, 11, 12, 13);
    }

    @Override
    public Set<Integer> getIgnoredSlots() {
        return Set.of();
    }
}

```

### Notes

* **Texture**: Refer to AbyssalLib for loading fonts and `TextureGlyph`s.
* **Offset**: Use `-8` in most cases.
* **Catalyst**: The provider item (e.g. Crafting Table) is passed to the parser automatically by the registry.
* **Probabilities**: Use `.probability(itemStack, expression/float)` to append chance notations to the lore of displayed items.
* **Paged Sections**: Pass a `PagedSection` to automatically chunk large lists of items into smaller navigable areas within the same view.
* **Recipe Stages**: For recipes requiring multi-step sequential processes, use `.addStage(RecipeStage)` to append standalone view definitions (supporting separate slots, textures, probabilities, and sections) that the player can paginate through.

---

## Registering Content

NER uses a lifecycle-based plugin registration system. You must implement `NerPlugin` and register it.

### 1. Create your Integration

```java
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.Registration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

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
        
        registry.addFilter("!", (query, item) -> item.getType().name().contains(query));

        registry.addDeduplicator(item -> {
            if (item.getType() == Material.POTION) return new ItemStack(Material.POTION);
            return item;
        });

        registry.addModifier((player, item) -> {
            if (shouldObscure(item)) applyObfuscation(item);
            return item;
        });

        registry.setNamespaceComparator("custom_namespace", (item1, item2) -> item1.getType().name().compareTo(item2.getType().name()));
    }
}

```

### 2. Hook into NER

Register your integration during your plugin's `onEnable()`:

```java
import com.github.darksoulq.ner.NeverEnoughRecipes;

@Override
public void onEnable() {
    NeverEnoughRecipes.registerPlugin(new MyNerIntegration());
}

```