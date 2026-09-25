package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.BrewingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public class BrewingCategory extends RecipeCategory<BrewingRecipe> {
    private static final int[] SLOTS = { 12, 30, 23 };

    @Override
    public Class<BrewingRecipe> getRecipeClass() {
        return BrewingRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(BrewingRecipe recipe, ItemStack catalyst) {
        return ParsedRecipeView.builder(Pack.BREWING, -8, catalyst)
            .setChoice(SLOTS[0], recipe.getIngredient())
            .setChoice(SLOTS[1], recipe.getInput())
            .set(SLOTS[2], recipe.getResult())
            .build();
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(23); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}