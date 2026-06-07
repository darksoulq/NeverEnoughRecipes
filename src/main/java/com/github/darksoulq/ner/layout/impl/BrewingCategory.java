package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public class BrewingCategory extends RecipeCategory<PotionMix> {
    private static final int[] SLOTS = { 12, 30, 23 };

    @Override
    public Class<PotionMix> getRecipeClass() {
        return PotionMix.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(PotionMix recipe, ItemStack catalyst) {
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