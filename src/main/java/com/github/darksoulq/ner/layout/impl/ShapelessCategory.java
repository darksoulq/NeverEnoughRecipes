package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class ShapelessCategory extends RecipeCategory<ShapelessRecipe> {
    private static final int[] SLOTS = { 11, 12, 13, 20, 21, 22, 29, 30, 31 };

    @Override
    public Class<ShapelessRecipe> getRecipeClass() {
        return ShapelessRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(ShapelessRecipe recipe, ItemStack catalyst) {
        Map<Integer, List<ItemStack>> slots = new HashMap<>();
        List<RecipeChoice> choices = recipe.getChoiceList();

        for (int i = 0; i < choices.size() && i < SLOTS.length; i++) {
            applyChoice(slots, SLOTS[i], choices.get(i));
        }
        slots.put(24, List.of(recipe.getResult()));
        return new ParsedRecipeView(slots, Pack.CRAFTING_TABLE, -8, catalyst);
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(24); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}