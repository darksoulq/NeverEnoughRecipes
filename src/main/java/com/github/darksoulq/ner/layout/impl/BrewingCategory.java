package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BrewingCategory extends RecipeCategory<PotionMix> {
    private static final int[] SLOTS = { 12, 30, 23 };

    @Override
    public Class<PotionMix> getRecipeClass() {
        return PotionMix.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(PotionMix recipe, ItemStack catalyst) {
        Map<Integer, List<ItemStack>> slots = new HashMap<>();
        applyChoice(slots, SLOTS[0], recipe.getIngredient());
        applyChoice(slots, SLOTS[1], recipe.getInput());
        slots.put(SLOTS[2], List.of(recipe.getResult()));
        return new ParsedRecipeView(slots, Pack.BREWING, -8, catalyst);
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(23); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Collections.emptySet(); }
}