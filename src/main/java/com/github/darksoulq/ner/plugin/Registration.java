package com.github.darksoulq.ner.plugin;

import com.github.darksoulq.ner.layout.RecipeCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Registration {
    void addCategory(RecipeCategory<?> category);
    void addCatalyst(Class<?> recipeClass, ItemStack catalyst);

    void addItem(ItemStack item);
    void addItem(String namespace, ItemStack item);
    void removeItem(ItemStack item);
    void removeItems(Predicate<ItemStack> predicate);

    void addDeduplicator(Function<ItemStack, ItemStack> deduplicator);
    void addModifier(BiFunction<Player, ItemStack, ItemStack> modifier);

    void addFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter);

    void setNamespaceComparator(String namespace, Comparator<ItemStack> comparator);

    void addRecipe(Object recipe);
    void removeRecipe(Object recipe);
    void removeRecipes(Predicate<Object> predicate);
}