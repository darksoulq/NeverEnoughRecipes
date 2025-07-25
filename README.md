# NeverEnoughRecipes

this plugin allows you to see/search recipes by items (like JEI!)
By default, the plugin supports every vanilla recipe type. (*Currently in 0.1.0 Brewing recipes are not included)

# Images
Soon.

# API for other plugins
Currently, no api class exists, however you can use internal methods to add your recipes!

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
}
```

For Texture please reference how to load Fonts and TextureGlyphs in AbyssalLib. (You can ask in the discord)
In case texture size is same as the Base texture provided on the GitHub, use an offset of -8.
ProviderItem is what block this recipe is used in, e.g for ShapedRecipes it is a Crafting table itemstack.

Register provider and recipes:
```Java
NerApi.registerLayout(new YourRecipeParser());

NerApi.registerItem("namespace", resultItem, recipeObject);
```
namespace can be anything, e.g. it could be your plugin name ("myplugin"), (if its a vanilla item its recommend to set namespace to "minecraft")

In case your recipe is from vanilla but the item is a custom item, add it to a namespace as such:
```java
NerApi.addItemToNamespace("namespace", ItemStack);
```

In the off chance that you are registering your recipes in bukkit but not allowing crafting in vanilla blocks.
```Java
NerApi.ignoreVanillaRecipe(NamespacedKey);
```
this MUST be called in onEnable() in case you are removing your (vanilla) recipes from default parsers (in case you wish to wrap it for your own parser)