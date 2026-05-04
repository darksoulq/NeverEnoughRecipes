package com.github.darksoulq.ner.registry;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class RecipeManager {
    private static final Map<Class<?>, RecipeCategory<?>> CATEGORIES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ItemStack> CATALYSTS = new ConcurrentHashMap<>();
    private static final List<Object> RAW_RECIPES = new ArrayList<>();

    private static final Map<ItemStack, List<ParsedRecipeView>> RECIPES_CACHE = new ConcurrentHashMap<>();
    private static final Map<ItemStack, List<ParsedRecipeView>> USES_CACHE = new ConcurrentHashMap<>();

    public static void clear() {
        CATEGORIES.clear();
        CATALYSTS.clear();
        RAW_RECIPES.clear();
        RECIPES_CACHE.clear();
        USES_CACHE.clear();
    }

    public static void addCategory(RecipeCategory<?> category) {
        CATEGORIES.put(category.getRecipeClass(), category);
    }

    public static void addCatalyst(Class<?> recipeClass, ItemStack catalyst) {
        CATALYSTS.put(recipeClass, catalyst.asOne());
    }

    public static void addRecipe(Object recipe) {
        RAW_RECIPES.add(recipe);
    }

    public static void removeRecipe(Object recipe) {
        RAW_RECIPES.remove(recipe);
    }

    public static void removeRecipes(Predicate<Object> predicate) {
        RAW_RECIPES.removeIf(predicate);
    }

    public static List<ParsedRecipeView> getRecipes(ItemStack item) {
        return RECIPES_CACHE.getOrDefault(IngredientManager.deduplicate(item.asOne()), Collections.emptyList());
    }

    public static List<ParsedRecipeView> getUses(ItemStack item) {
        return USES_CACHE.getOrDefault(IngredientManager.deduplicate(item.asOne()), Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public static void compile() {
        RECIPES_CACHE.clear();
        USES_CACHE.clear();

        for (Object recipe : RAW_RECIPES) {
            RecipeCategory<?> rawCat = null;
            for (Map.Entry<Class<?>, RecipeCategory<?>> entry : CATEGORIES.entrySet()) {
                if (entry.getKey().isAssignableFrom(recipe.getClass())) {
                    rawCat = entry.getValue();
                    break;
                }
            }
            if (rawCat == null) continue;

            RecipeCategory<Object> category = (RecipeCategory<Object>) rawCat;
            ItemStack catalyst = CATALYSTS.getOrDefault(category.getRecipeClass(), new ItemStack(Material.BARRIER));

            ParsedRecipeView parsed = category.parseRecipe(recipe, catalyst);
            Set<Integer> results = category.getResultSlots();
            Set<Integer> ignored = category.getIgnoredSlots();

            boolean producesHidden = false;
            for (Map.Entry<Integer, List<ItemStack>> entry : parsed.slots().entrySet()) {
                if (results.contains(entry.getKey())) {
                    for (ItemStack item : entry.getValue()) {
                        if (IngredientManager.isHidden(item)) producesHidden = true;
                    }
                }
            }
            if (producesHidden) continue;

            parsed.slots().forEach((slot, items) -> {
                boolean isIgnored = ignored.contains(slot);
                boolean isResult = results.contains(slot);
                for (ItemStack item : items) {
                    ItemStack normalized = IngredientManager.deduplicate(item.asOne());
                    if (isIgnored || IngredientManager.isHidden(normalized)) continue;

                    IngredientManager.addItem(normalized);

                    if (isResult) {
                        List<ParsedRecipeView> list = RECIPES_CACHE.computeIfAbsent(normalized, k -> new ArrayList<>());
                        if (!list.contains(parsed)) list.add(parsed);
                    } else {
                        List<ParsedRecipeView> list = USES_CACHE.computeIfAbsent(normalized, k -> new ArrayList<>());
                        if (!list.contains(parsed)) list.add(parsed);
                    }
                }
            });
        }
    }
}