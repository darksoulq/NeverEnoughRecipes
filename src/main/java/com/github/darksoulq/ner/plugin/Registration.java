package com.github.darksoulq.ner.plugin;

import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ItemGroup;
import com.github.darksoulq.ner.registry.CraftabilityChecker;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
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

    void addItemGroup(String id, Component title, List<ItemStack> items, boolean animate);

    void addDeduplicator(Function<ItemStack, ItemStack> deduplicator);
    void addModifier(BiFunction<Player, ItemStack, ItemStack> modifier);

    void addFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter);

    void setNamespaceComparator(String namespace, Comparator<Either<ItemStack, ItemGroup>> comparator);

    void addRecipe(Object recipe);
    void removeRecipe(Object recipe);
    void removeRecipes(Predicate<Object> predicate);

    <T> void addCraftabilityChecker(Class<T> recipeClass, CraftabilityChecker<T> checker);
}