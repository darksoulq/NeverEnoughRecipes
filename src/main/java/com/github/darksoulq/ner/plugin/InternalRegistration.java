package com.github.darksoulq.ner.plugin;

import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ItemGroup;
import com.github.darksoulq.ner.registry.CraftabilityChecker;
import com.github.darksoulq.ner.registry.GroupManager;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class InternalRegistration implements Registration {
    @Override
    public void addCategory(RecipeCategory<?> category) {
        RecipeManager.addCategory(category);
    }

    @Override
    public void addCatalyst(Class<?> recipeClass, ItemStack catalyst) {
        RecipeManager.addCatalyst(recipeClass, catalyst);
    }

    @Override
    public void addItem(ItemStack item) {
        IngredientManager.addItem(item);
    }

    @Override
    public void addItem(String namespace, ItemStack item) {
        IngredientManager.addItem(namespace, item);
    }

    @Override
    public void removeItem(ItemStack item) {
        IngredientManager.removeItem(item);
    }

    @Override
    public void removeItems(Predicate<ItemStack> predicate) {
        IngredientManager.removeItems(predicate);
    }

    @Override
    public void addItemGroup(String id, Component title, List<ItemStack> items, boolean animate) {
        GroupManager.addGroup(new ItemGroup(id, title, items, animate));
    }

    @Override
    public void addDeduplicator(Function<ItemStack, ItemStack> deduplicator) {
        IngredientManager.addDeduplicator(deduplicator);
    }

    @Override
    public void addModifier(BiFunction<Player, ItemStack, ItemStack> modifier) {
        IngredientManager.addModifier(modifier);
    }

    @Override
    public void addFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter) {
        IngredientManager.addFilter(prefix, filter);
    }

    @Override
    public void setNamespaceComparator(String namespace, Comparator<Either<ItemStack, ItemGroup>> comparator) {
        IngredientManager.setNamespaceComparator(namespace, comparator);
    }

    @Override
    public void addRecipe(Object recipe) {
        RecipeManager.addRecipe(recipe);
    }

    @Override
    public void removeRecipe(Object recipe) {
        RecipeManager.removeRecipe(recipe);
    }

    @Override
    public void removeRecipes(Predicate<Object> predicate) {
        RecipeManager.removeRecipes(predicate);
    }

    @Override
    public <T> void addCraftabilityChecker(Class<T> recipeClass, CraftabilityChecker<T> checker) {
        RecipeManager.addCraftabilityChecker(recipeClass, checker);
    }
}