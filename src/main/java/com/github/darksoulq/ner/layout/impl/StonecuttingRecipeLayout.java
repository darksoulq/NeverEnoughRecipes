package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StonecuttingRecipeLayout extends RecipeLayout<StonecuttingRecipe> {
        private static final int TARGET_SLOT = 31;

    @Override
    public Class<StonecuttingRecipe> getRecipeClass() {
        return StonecuttingRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(StonecuttingRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        if (recipe.getInputChoice() instanceof RecipeChoice.ExactChoice exact) {
            slotMap.put(TARGET_SLOT, exact.getChoices());
        } else if (recipe.getInputChoice() instanceof RecipeChoice.MaterialChoice mat) {
            slotMap.put(TARGET_SLOT, mat.getChoices().stream().map(ItemStack::new).toList());
        }
        return new ParsedRecipeView(slotMap, Pack.ONE_SLOT, -8, new ItemStack(Material.STONECUTTER));
    }
}
