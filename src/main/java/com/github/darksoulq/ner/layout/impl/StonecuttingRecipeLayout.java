package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StonecuttingRecipeLayout extends RecipeLayout<StonecuttingRecipe> {
        private static final int[] TARGET_SLOTS = {20, 24};

    @Override
    public Class<StonecuttingRecipe> getRecipeClass() {
        return StonecuttingRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(StonecuttingRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        setItems(slotMap, TARGET_SLOTS[0], recipe.getInputChoice());
        slotMap.put(TARGET_SLOTS[1], List.of(recipe.getResult()));
        return new ParsedRecipeView(slotMap, Pack.STONE_CUTTER, -8, new ItemStack(Material.STONECUTTER));
    }

    @Override
    public Set<Integer> getOutputSlots() {
        return Set.of(24);
    }
}
