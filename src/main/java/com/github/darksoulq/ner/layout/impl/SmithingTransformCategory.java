package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingTransformRecipe;

import java.util.*;

public class SmithingTransformCategory extends RecipeCategory<SmithingTransformRecipe> {
    private static final int[] SLOTS = { 20, 21, 22, 24 };

    @Override
    public Class<SmithingTransformRecipe> getRecipeClass() {
        return SmithingTransformRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(SmithingTransformRecipe recipe, ItemStack catalyst) {
        Map<Integer, List<ItemStack>> slots = new HashMap<>();
        applyChoice(slots, SLOTS[0], recipe.getTemplate());
        applyChoice(slots, SLOTS[1], recipe.getBase());
        applyChoice(slots, SLOTS[2], recipe.getAddition());
        slots.put(SLOTS[3], List.of(recipe.getResult()));
        return new ParsedRecipeView(slots, Pack.SMITHING, -8, catalyst);
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(24); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}