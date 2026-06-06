package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingTransformRecipe;

import java.util.Collections;
import java.util.Set;

public class SmithingTransformCategory extends RecipeCategory<SmithingTransformRecipe> {
    private static final int[] SLOTS = { 20, 21, 22, 24 };

    @Override
    public Class<SmithingTransformRecipe> getRecipeClass() {
        return SmithingTransformRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(SmithingTransformRecipe recipe, ItemStack catalyst) {
        return ParsedRecipeView.builder(Pack.SMITHING, -8, catalyst)
            .setChoice(SLOTS[0], recipe.getTemplate())
            .setChoice(SLOTS[1], recipe.getBase())
            .setChoice(SLOTS[2], recipe.getAddition())
            .set(SLOTS[3], recipe.getResult())
            .build();
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(24); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}