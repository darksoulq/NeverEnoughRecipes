package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ShapelessCategory extends RecipeCategory<ShapelessRecipe> {
    private static final int[] SLOTS = { 11, 12, 13, 20, 21, 22, 29, 30, 31 };

    @Override
    public Class<ShapelessRecipe> getRecipeClass() {
        return ShapelessRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(ShapelessRecipe recipe, ItemStack catalyst) {
        ParsedRecipeView.Builder builder = ParsedRecipeView.builder(Pack.CRAFTING_TABLE, -8, catalyst);
        List<RecipeChoice> choices = recipe.getChoiceList();

        for (int i = 0; i < choices.size() && i < SLOTS.length; i++) {
            builder.setChoice(SLOTS[i], choices.get(i));
        }

        return builder.set(24, recipe.getResult()).build();
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(24); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}