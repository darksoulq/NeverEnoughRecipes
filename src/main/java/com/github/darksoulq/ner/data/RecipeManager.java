package com.github.darksoulq.ner.data;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.layout.RecipeLayoutRegistry;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeManager {
    private static final Map<ItemStack, List<Object>> recipeMap = new HashMap<>();
    private static final Set<String> IGNORED_RECIPES = new HashSet<>();

    public static void loadVanillaRecipes() {
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof Keyed keyed) {
                if (IGNORED_RECIPES.contains(keyed.getKey().toString())) return;
            }
            if (RecipeLayoutRegistry.hasLayout(recipe.getClass())) {
                if (recipe.getResult().isSimilar(ItemStack.of(recipe.getResult().getType()))) {
                    NamespacedFilterManager.addItem("minecraft", recipe.getResult().clone());
                }
                recipeMap.computeIfAbsent(recipe.getResult().asOne(), k -> new ArrayList<>()).add(recipe);
            }
        });
    }

    public static void addCustomRecipe(Object recipe, ItemStack result) {
        if (!RecipeLayoutRegistry.hasLayout(recipe.getClass())) {
            throw new IllegalArgumentException(
                    "No RecipeLayout registered for: " + recipe.getClass().getName());
        }
        recipeMap.computeIfAbsent(result, k -> new ArrayList<>()).add(recipe);
    }

    public static void addIgnoredRecipe(NamespacedKey key) {
        IGNORED_RECIPES.add(key.toString());
    }

    public static Set<ItemStack> getAllItems() {
        return recipeMap.keySet();
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
        return recipeMap.getOrDefault(result, List.of());
    }
}
