package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmokingRecipeLayout extends RecipeLayout<SmokingRecipe> {
    private static final int TARGET_SLOT = 22;
    @Override
    public Class<SmokingRecipe> getRecipeClass() {
        return SmokingRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(SmokingRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        RecipeChoice choice = recipe.getInputChoice();
        if (choice.equals(RecipeChoice.empty())) return new ParsedRecipeView(new HashMap<>(), Pack.COOKING, 0, new ItemStack(Material.FURNACE));
        if (choice instanceof RecipeChoice.MaterialChoice material) {
            slotMap.put(TARGET_SLOT, material.getChoices().stream().map(ItemStack::new).toList());
        } else if (choice instanceof RecipeChoice.ExactChoice exact) {
            slotMap.put(TARGET_SLOT, exact.getChoices());
        }
        return new ParsedRecipeView(slotMap, Pack.COOKING, -8, new ItemStack(Material.SMOKER));
    }
}
