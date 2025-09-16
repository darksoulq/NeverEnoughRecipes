package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.TransmuteRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransmuteRecipeLayout extends RecipeLayout<TransmuteRecipe> {
    private static final int[] TARGET_SLOTS = {
            11, 12, 13,
            20, 21, 22,
            29, 30, 31
    };

    @Override
    public Class<TransmuteRecipe> getRecipeClass() {
        return TransmuteRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(TransmuteRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();

        List<RecipeChoice> choices = List.of(recipe.getInput(), recipe.getMaterial());

        for (int i = 0; i < choices.size(); i++) {
            RecipeChoice choice = choices.get(i);
            int slot = TARGET_SLOTS[i];

            if (choice == null || choice.equals(RecipeChoice.empty())) continue;

            setItems(slotMap, slot, choice);
        }

        slotMap.put(24, List.of(recipe.getResult()));

        return new ParsedRecipeView(slotMap, Pack.CRAFTING_TABLE, -8, new ItemStack(Material.CRAFTING_TABLE));
    }

    @Override
    public Set<Integer> getOutputSlots() {
        return Set.of(24);
    }
}
