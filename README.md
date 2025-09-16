[![Modrinth Downloads](https://img.shields.io/modrinth/dt/neverenoughrecipes?style=flat-square&logo=modrinth&link=https%3A%2F%2Fmodrinth.com%2Fplugin%2Fneverenoughrecipes)](https://modrinth.com/plugin/neverenoughrecipes/)[![Spiget Downloads](https://img.shields.io/spiget/downloads/127199?style=flat-square&logo=spigotmc&link=https%3A%2F%2Fwww.spigotmc.org%2Fresources%2Fneverenoughrecipes.127199%2F)](https://www.spigotmc.org/resources/neverenoughrecipes.127199/)[![Discord](https://img.shields.io/discord/1204752282919370812?style=flat-square&logo=discord&link=https%3A%2F%2Fdiscord.gg%2Fe35gP423vN)](https://discord.gg/e35gP423vN)
<img src="https://www.dropbox.com/scl/fi/v5tpifa40tr50ahefb169/NerPage.png?rlkey=j9erlhnh3eiutha33us6t9cdo&st=887cnkyo&dl=1" alt="NerPage" width="100%" height="99%">

# Credits
[JEI](https://modrinth.com/mod/jei) For the initial idea and many design choices
[PolyDex](https://modrinth.com/mod/polydex) For specific design choices regarding the gui

# API for other plugins
Repository:
```gradle
maven { url 'https://jitpack.io' }
```

Dependency:
```gradle
implementation('com.github.darksoulq:NeverEnoughRecipes:<version>')
```
Replace <version> with latest github release

Making your recipe parser:

```Java
import java.util.HashMap;

public class YourRecipeParser implements RecipeLayot<YouRecipeClass> {
    @Override
    public Class<YourRecipeClass> getRecipeClass() {
        return YourRecipeClass.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(YourRecipeClass recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        // fill slotMap
        return new ParsedRecipeView(slotMap, Texture, offset, ProviderItem);
    }
            
    @Override
    public Set<Integer> getOutpuutSlots() {
        // Return output slots, the items in these slots will have this recipe added as a "use"
    }
}
```

For Texture please reference how to load Fonts and TextureGlyphs in AbyssalLib. (You can ask in the discord)
In case texture size is same as the Base texture provided on the GitHub, use an offset of -8.
ProviderItem is what block this recipe is used in, e.g for ShapedRecipes it is a Crafting table itemstack.

Register items, providers and recipes:
```java
NerApi.addItemToNamespace(ItemStack, "namespace");
```
This adds the provided stack to provided namespace and makes it visible in main meenu
namespace can be anything, e.g. it could be your plugin name ("myplugin")
```java
NerApi.registerRecipe(ResultStack, Recipe);
```
this assigns the recipe to provided result, you can assign samme recipe to multiple results.
```java
NerApi.registerLayout(RecipeLayout);
```
this registers your given layout.

In the case your item has no namespace, add it as such:
```java
NerApi.addItem(item);
```

You can also register menu filters foor search menu!
```java
NerApi.registerMenuFilter("prefix", BiFunction<String, ItemStack, Boolean>);
```
The BiFunction being the filter

In the off chance that you are registering your recipes in bukkit but not allowing crafting in vanilla blocks.
```Java
NerApi.ignoreVanillaRecipe(NamespacedKey);
```
this MUST be called in onEnable() in case you are removing your (vanilla) recipes from default parsers (in case you wish to wrap it for your own parser)