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
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Registration {
    interface NamespaceContext {
        void set(String namespace, ItemStack item);
        void set(String namespace, Iterable<ItemStack> items);
        List<ItemStack> getItems();
    }

    void addCategory(RecipeCategory<?> category);
    void addCatalyst(Class<?> recipeClass, ItemStack catalyst);

    void setNamespace(String namespace, ItemStack item);
    void setNamespaces(Consumer<NamespaceContext> provider);

    void addItem(ItemStack item);
    void addItem(String namespace, ItemStack item);
    void removeItem(ItemStack item);
    void removeItems(Predicate<ItemStack> predicate);

    void addItemGroup(String id, Component title, List<ItemStack> items, boolean animate);

    void addDeduplicator(Function<ItemStack, ItemStack> deduplicator);
    void addModifier(BiFunction<Player, ItemStack, ItemStack> modifier);
    void addVisibilityRule(BiPredicate<Player, ItemStack> rule);

    void addFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter);
    void addNestedFilter(String prefix, Function<Predicate<ItemStack>, Predicate<ItemStack>> operator);

    void setNamespaceComparator(String namespace, Comparator<Either<ItemStack, ItemGroup>> comparator);

    void addRecipe(Object recipe);
    void removeRecipe(Object recipe);
    void removeRecipes(Predicate<Object> predicate);

    <T> void addCraftabilityChecker(Class<T> recipeClass, CraftabilityChecker<T> checker);
}