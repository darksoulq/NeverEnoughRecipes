package com.github.darksoulq.ner.registry;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.PagedSection;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.RecipeStage;
import com.github.darksoulq.ner.util.CraftabilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class RecipeManager {
    private static final Map<Class<?>, RecipeCategory<?>> CATEGORIES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ItemStack> CATALYSTS = new ConcurrentHashMap<>();
    private static final List<Object> RAW_RECIPES = new ArrayList<>();

    private static final Map<Class<?>, CraftabilityChecker<?>> CRAFTABILITY_CHECKERS = new ConcurrentHashMap<>();
    private static final Map<ParsedRecipeView, Object> VIEW_TO_RECIPE = new ConcurrentHashMap<>();
    private static final Map<Object, RecipeCategory<?>> RECIPE_TO_CATEGORY = new ConcurrentHashMap<>();

    private static final Map<ItemStack, List<ParsedRecipeView>> RECIPES_CACHE = new ConcurrentHashMap<>();
    private static final Map<ItemStack, List<ParsedRecipeView>> USES_CACHE = new ConcurrentHashMap<>();
    private static final List<ParsedRecipeView> ALL_COMPILED_VIEWS = new ArrayList<>();

    public static void clear() {
        CATEGORIES.clear();
        CATALYSTS.clear();
        RAW_RECIPES.clear();
        CRAFTABILITY_CHECKERS.clear();
        VIEW_TO_RECIPE.clear();
        RECIPE_TO_CATEGORY.clear();
        RECIPES_CACHE.clear();
        USES_CACHE.clear();
        ALL_COMPILED_VIEWS.clear();
    }

    public static void addCategory(RecipeCategory<?> category) {
        CATEGORIES.put(category.getRecipeClass(), category);
    }

    public static Map<Class<?>, RecipeCategory<?>> getCategories() {
        return CATEGORIES;
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

    public static <T> void addCraftabilityChecker(Class<T> recipeClass, CraftabilityChecker<T> checker) {
        CRAFTABILITY_CHECKERS.put(recipeClass, checker);
    }

    public static List<ParsedRecipeView> getRecipes(ItemStack item) {
        return RECIPES_CACHE.getOrDefault(IngredientManager.deduplicate(item.asOne()), Collections.emptyList());
    }

    public static List<ParsedRecipeView> getUses(ItemStack item) {
        return USES_CACHE.getOrDefault(IngredientManager.deduplicate(item.asOne()), Collections.emptyList());
    }

    public static List<ParsedRecipeView> getAllRecipes() {
        return Collections.unmodifiableList(ALL_COMPILED_VIEWS);
    }

    public static Object getRawRecipe(ParsedRecipeView view) {
        return VIEW_TO_RECIPE.get(view);
    }

    public static RecipeCategory<?> getCategory(ParsedRecipeView view) {
        Object raw = VIEW_TO_RECIPE.get(view);
        return raw != null ? RECIPE_TO_CATEGORY.get(raw) : null;
    }

    @SuppressWarnings("unchecked")
    public static boolean isCraftable(Player player, ParsedRecipeView view) {
        if (view == null) return false;
        Object raw = VIEW_TO_RECIPE.get(view);
        if (raw == null) return false;

        RecipeCategory<?> cat = RECIPE_TO_CATEGORY.get(raw);
        if (cat != null && !cat.isCraftableCategory()) {
            return false;
        }

        CraftabilityChecker<Object> checker = null;
        for (Map.Entry<Class<?>, CraftabilityChecker<?>> entry : CRAFTABILITY_CHECKERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(raw.getClass())) {
                checker = (CraftabilityChecker<Object>) entry.getValue();
                break;
            }
        }

        if (checker != null) {
            return checker.canCraft(player, raw);
        } else if (cat != null) {
            return defaultCraftCheck(player, view, cat);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean isCraftable(Player player, ItemStack item) {
        List<ParsedRecipeView> recipes = getRecipes(item);
        if (recipes.isEmpty()) return false;

        for (ParsedRecipeView view : recipes) {
            if (isCraftable(player, view)) return true;
        }
        return false;
    }

    private static boolean defaultCraftCheck(Player player, ParsedRecipeView view, RecipeCategory<?> category) {
        Set<Integer> results = category.getResultSlots();
        Set<Integer> ignored = category.getIgnoredSlots();
        List<RecipeChoice> choices = new ArrayList<>();

        for (RecipeStage stage : view.stages()) {
            for (Map.Entry<Integer, List<ItemStack>> entry : stage.slots().entrySet()) {
                if (!results.contains(entry.getKey()) && !ignored.contains(entry.getKey())) {
                    List<ItemStack> valid = entry.getValue();
                    if (valid != null && !valid.isEmpty()) {
                        choices.add(new RecipeChoice.ExactChoice(valid));
                    }
                }
            }

            for (PagedSection section : stage.pagedSections()) {
                if (section.slots() != null && section.slots().length > 0) {
                    int firstSlot = section.slots()[0];
                    if (!results.contains(firstSlot) && !ignored.contains(firstSlot)) {
                        List<ItemStack> valid = section.items();
                        if (valid != null && !valid.isEmpty()) {
                            choices.add(new RecipeChoice.ExactChoice(valid));
                        }
                    }
                }
            }
        }

        return CraftabilityUtil.hasIngredients(player, choices);
    }

    @SuppressWarnings("unchecked")
    public static void compile() {
        RECIPES_CACHE.clear();
        USES_CACHE.clear();
        VIEW_TO_RECIPE.clear();
        RECIPE_TO_CATEGORY.clear();
        ALL_COMPILED_VIEWS.clear();

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
            if (parsed == null) continue;

            VIEW_TO_RECIPE.put(parsed, recipe);
            RECIPE_TO_CATEGORY.put(recipe, rawCat);

            Set<Integer> results = category.getResultSlots();
            Set<Integer> ignored = category.getIgnoredSlots();

            boolean producesHidden = false;

            for (RecipeStage stage : parsed.stages()) {
                for (Map.Entry<Integer, List<ItemStack>> entry : stage.slots().entrySet()) {
                    if (results.contains(entry.getKey())) {
                        for (ItemStack item : entry.getValue()) {
                            if (IngredientManager.isHidden(item)) {
                                producesHidden = true;
                                break;
                            }
                        }
                    }
                    if (producesHidden) break;
                }

                if (!producesHidden) {
                    for (PagedSection section : stage.pagedSections()) {
                        if (section.slots() != null && section.slots().length > 0) {
                            int firstSlot = section.slots()[0];
                            if (results.contains(firstSlot)) {
                                for (ItemStack item : section.items()) {
                                    if (IngredientManager.isHidden(item)) {
                                        producesHidden = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (producesHidden) break;
                    }
                }
                if (producesHidden) break;
            }

            if (producesHidden) continue;

            ALL_COMPILED_VIEWS.add(parsed);

            for (RecipeStage stage : parsed.stages()) {
                processItems(stage.slots(), results, ignored, parsed);
                for (PagedSection section : stage.pagedSections()) {
                    if (section.slots() != null && section.slots().length > 0) {
                        int slot = section.slots()[0];
                        processSectionItems(section.items(), slot, results, ignored, parsed);
                    }
                }
            }
        }
    }

    private static void processItems(Map<Integer, List<ItemStack>> slots, Set<Integer> results, Set<Integer> ignored, ParsedRecipeView parsed) {
        slots.forEach((slot, items) -> {
            boolean isIgnored = ignored.contains(slot);
            boolean isResult = results.contains(slot);
            for (ItemStack item : items) {
                processSingleItem(item, isIgnored, isResult, parsed);
            }
        });
    }

    private static void processSectionItems(List<ItemStack> items, int reprSlot, Set<Integer> results, Set<Integer> ignored, ParsedRecipeView parsed) {
        if (items == null) return;
        boolean isIgnored = ignored.contains(reprSlot);
        boolean isResult = results.contains(reprSlot);
        for (ItemStack item : items) {
            processSingleItem(item, isIgnored, isResult, parsed);
        }
    }

    private static void processSingleItem(ItemStack item, boolean isIgnored, boolean isResult, ParsedRecipeView parsed) {
        ItemStack normalized = IngredientManager.deduplicate(item.asOne());
        if (isIgnored || IngredientManager.isHidden(normalized)) return;

        IngredientManager.addItem(normalized);

        List<ParsedRecipeView> list;
        if (isResult) {
            list = RECIPES_CACHE.computeIfAbsent(normalized, k -> new ArrayList<>());
        } else {
            list = USES_CACHE.computeIfAbsent(normalized, k -> new ArrayList<>());
        }
        if (!list.contains(parsed)) list.add(parsed);
    }
}