package com.github.darksoulq.ner.data;

import com.github.darksoulq.ner.NerApi;
import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.layout.RecipeLayoutRegistry;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.*;

public class RecipeManager {
    private static final Map<ItemStack, List<Object>> RECIPES = new HashMap<>();
    private static final Map<ItemStack, List<Object>> USES = new HashMap<>();
    private static final Set<String> IGNORED_RECIPES = new HashSet<>();

    public static void loadVanillaRecipes() {
        Registry<ItemType> typeReg = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
        for (Tag<ItemType> tag : typeReg.getTags()) {
            for (ItemType type : tag.resolve(typeReg)) {
                ItemStack item = type.createItemStack();
                NerApi.addItem(item);
                if (!item.isSimilar(ItemStack.of(item.getType()))) continue;
                NerApi.addItemToNamespace("minecraft", item);
            }
        }
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof Keyed keyed) {
                if (IGNORED_RECIPES.contains(keyed.getKey().toString())) return;
            }
            if (RecipeLayoutRegistry.hasLayout(recipe.getClass())) {
                NerApi.registerRecipe(recipe.getResult(), recipe);
            }
        });
    }

    public static void addCustomRecipe(Object recipe, ItemStack result) {
        if (!RecipeLayoutRegistry.hasLayout(recipe.getClass())) {
            throw new IllegalArgumentException(
                    "No RecipeLayout registered for: " + recipe.getClass().getName());
        }
        RECIPES.computeIfAbsent(result, k -> new ArrayList<>()).add(recipe);
        RecipeLayout layout = RecipeLayoutRegistry.getLayout(recipe.getClass());
        if (layout == null) return;
        for (Map.Entry<Integer, List<ItemStack>> entry : parse(recipe).getSlotMap().entrySet()) {
            if (layout.getOutputSlots().contains(entry.getKey())) continue;
            entry.getValue().forEach(k -> addCustomUse(k, recipe));
        }
    }

    public static void addCustomUse(ItemStack used, Object recipe) {
        List<Object> recipes = USES.computeIfAbsent(used.asOne(), k -> new ArrayList<>());
        if (recipes.contains(recipe)) return;
        recipes.add(recipe);
    }

    public static void addItem(ItemStack result) {
        RECIPES.put(result, new ArrayList<>());
    }

    public static void addIgnoredRecipe(NamespacedKey key) {
        IGNORED_RECIPES.add(key.toString());
    }

    public static Set<ItemStack> getAllItems() {
        return RECIPES.keySet();
    }

    @SuppressWarnings("unchecked")
    public static ParsedRecipeView parse(Object recipe) {
        RecipeLayout layout = RecipeLayoutRegistry.getLayout(recipe.getClass());
        if (layout == null) {
            throw new IllegalStateException(
                    "No layout for class: " + recipe.getClass().getName());
        }
        return ((RecipeLayout<Object>) layout).parseRecipe(recipe);
    }

    public static List<Object> getRecipesForResult(ItemStack result) {
        return RECIPES.getOrDefault(result, List.of());
    }

    public static List<Object> getUsesForItem(ItemStack item) {
        return USES.getOrDefault(item, List.of());
    }
}
