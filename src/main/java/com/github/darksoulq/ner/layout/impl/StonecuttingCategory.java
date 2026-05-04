package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.*;

public class StonecuttingCategory extends RecipeCategory<StonecuttingRecipe> {
    private static final int[] SLOTS = { 20, 24 };

    @Override
    public Class<StonecuttingRecipe> getRecipeClass() {
        return StonecuttingRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(StonecuttingRecipe recipe, ItemStack catalyst) {
        Map<Integer, List<ItemStack>> slots = new HashMap<>();
        applyChoice(slots, SLOTS[0], recipe.getInputChoice());
        slots.put(SLOTS[1], List.of(recipe.getResult()));
        return new ParsedRecipeView(slots, Pack.STONE_CUTTER, -8, catalyst);
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(24); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}