# Custom Recipe Categories
<link-summary>Learn how to define visual layouts, grid slots, probabilities, multi-step stages, and paginated sections for your custom recipes.</link-summary>

The layout system in NER is built around the `RecipeCategory<T>` class. It translates raw recipe data into a structured format that the GUI can render.

By defining a category, you can map inputs and outputs to specific inventory slots, display custom textures from AbyssalLib, append probability tooltips, and create multi-stage or paginated recipe sequences.

### 1. Creating a Category

To define a custom layout, create a class that extends `RecipeCategory<T>` where `T` is your custom recipe class. You must implement four abstract methods.

<code-block lang="java">
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.RecipeStage;
import com.github.darksoulq.ner.model.PagedSection;
import com.github.darksoulq.ner.model.SectionButton;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.List;

public class MyCustomCategory extends RecipeCategory&lt;MyRecipeClass&gt; {

    @Override
    public Class&lt;MyRecipeClass&gt; getRecipeClass() {
        return MyRecipeClass.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(MyRecipeClass recipe, ItemStack catalyst) {
        // Assume 'MyTextures.CUSTOM_BG' is a Font.TextureGlyph you registered
        ParsedRecipeView.Builder builder = ParsedRecipeView.builder(MyTextures.CUSTOM_BG, -8, catalyst);
        
        // Setting a static item
        builder.set(20, recipe.getInput());
        
        // Setting an animated list of items or Bukkit RecipeChoice
        builder.setChoice(21, recipe.getBukkitChoice());
        builder.set(24, recipe.getResult());
        
        // Adding probability lore to an item
        builder.probability(recipe.getResult(), "50%");
        
        // Adding a paginated section for multiple outputs
        builder.addSection(new PagedSection(
            new int[]{10, 11, 12}, 
            recipe.getPossibleExtraOutputs(), 
            new SectionButton(9, null), // Pass an ItemStack, or null for default arrow
            new SectionButton(13, null)     
        ));

        return builder.build();
    }

    @Override
    public Set&lt;Integer&gt; getResultSlots() {
        // Used by CraftabilityCheckers to ignore these slots when checking (and manager to detect whether its a USE of the item or its RECIPE)
        return Set.of(24, 10, 11, 12);
    }

    @Override
    public Set&lt;Integer&gt; getIgnoredSlots() {
        // Used for decorative items like XP icons that aren't inputs or outputs
        return Set.of();
    }
}
</code-block>

<note>
By default, <code>isCraftableCategory()</code> returns true. If your category represents something that cannot be crafted by players (like a natural world generation table), you can override it to return false so it doesn't show up in the "Craftable" main menu filter.
</note>

### 2. The Builder Methods

The `ParsedRecipeView.Builder` (and its internal `RecipeStage.Builder`) provide several ways to parse your items securely.

#### Basic Slots and Choices
* `set(int slot, ItemStack item)` — Sets a single item.
* `set(int slot, List<ItemStack> items)` — Sets multiple items in one slot. The GUI will automatically animate and cycle through them.
* `setChoice(int slot, RecipeChoice choice)` — Automatically unpacks a Bukkit `RecipeChoice.MaterialChoice` or `RecipeChoice.ExactChoice` into an animated list.

<tip>
The <code>RecipeCategory</code> class also provides a protected <code>applyChoice(Map&lt;Integer, List&lt;ItemStack&gt;&gt; map, int slot, RecipeChoice choice)</code> helper method if you ever need to manually resolve a Bukkit choice into a map before building.
</tip>

#### Probabilities
Use `.probability()` to automatically append a formatted `Chance: X%` string to the bottom of the item's lore when viewed in the GUI.
* `.probability(ItemStack item, float chance)`
* `.probability(ItemStack item, String expression)`
* `.probability(List<ItemStack> items, float chance)`
* `.probability(List<ItemStack> items, String expression)`

#### Paged Sections
If your recipe produces or accepts a large list of items, use `PagedSection`. It takes:
1. `int[] slots`: The GUI slots to populate.
2. `List<ItemStack> items`: The massive list of items to display.
3. `SectionButton prevButton` / `SectionButton nextButton`: Defined using `new SectionButton(slot, ItemStack)`.

NER will automatically paginate the list across your defined slots and make the buttons functional.

#### Multi-Stage Recipes
If your recipe is a complex, multi-step process, you can append additional sequential stages using `RecipeStage`. The `ParsedRecipeView.Builder` acts as the primary stage, but you can append more via `.addStage()`.

<code-block lang="java">
RecipeStage extraStage = RecipeStage.builder(MyTextures.STAGE_TWO_BG, -8)
    .set(20, recipe.getStageTwoInput())
    .set(24, recipe.getStageTwoResult())
    .build();

builder.addStage(extraStage);
</code-block>

Players will see a "Stage 1" and "Stage 2" indicator at the top of the Recipe Viewer, allowing them to flip chronologically through the process.

### 3. Registering Your Category

Once your layout is complete, register it in your `NerPlugin` integration class alongside the "Catalyst" (the block or item that processes the recipe).

<code-block lang="java">
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.Registration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MyCategoryIntegration implements NerPlugin {

    @Override
    public void register(Registration registry) {
        registry.addCategory(new MyCustomCategory());
        registry.addCatalyst(MyRecipeClass.class, new ItemStack(Material.SMITHING_TABLE));
    }
}
</code-block>