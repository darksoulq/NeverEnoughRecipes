package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ShapedCategory extends RecipeCategory<ShapedRecipe> {
    private static final int[] SLOTS = { 11, 12, 13, 20, 21, 22, 29, 30, 31 };

    @Override
    public Class<ShapedRecipe> getRecipeClass() { return ShapedRecipe.class; }

    @Override
    public ParsedRecipeView parseRecipe(ShapedRecipe recipe, ItemStack catalyst) {
        ParsedRecipeView.Builder builder = ParsedRecipeView.builder(Pack.CRAFTING_TABLE, -8, catalyst);
        String[] shape = recipe.getShape();
        Map<Character, RecipeChoice> ing = recipe.getChoiceMap();

        String[] normalized = new String[]{"   ", "   ", "   "};
        for (int i = 0; i < shape.length; i++) {
            normalized[i] = String.format("%-3s", shape[i]);
        }

        int index = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++, index++) {
                builder.setChoice(SLOTS[index], ing.get(normalized[r].charAt(c)));
            }
        }

        return builder.set(24, recipe.getResult()).build();
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(24); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}