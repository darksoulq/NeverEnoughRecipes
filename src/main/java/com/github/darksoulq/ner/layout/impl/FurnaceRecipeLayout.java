package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnaceRecipeLayout extends RecipeLayout<FurnaceRecipe> {
    private static final int TARGET_SLOT = 22;
    @Override
    public Class<FurnaceRecipe> getRecipeClass() {
        return FurnaceRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(FurnaceRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        RecipeChoice choice = recipe.getInputChoice();
        if (choice.equals(RecipeChoice.empty())) return new ParsedRecipeView(new HashMap<>(), Pack.COOKING, 0, new ItemStack(Material.FURNACE));
        setItems(slotMap, TARGET_SLOT, choice);
        return new ParsedRecipeView(slotMap, Pack.COOKING, -8, new ItemStack(Material.FURNACE));
    }
}
