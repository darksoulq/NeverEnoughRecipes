package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShapelessRecipeLayout extends RecipeLayout<ShapelessRecipe> {
    private static final int[] TARGET_SLOTS = {
            11, 12, 13,
            20, 21, 22,
            29, 30, 31
    };

    @Override
    public Class<ShapelessRecipe> getRecipeClass() {
        return ShapelessRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(ShapelessRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();

        List<RecipeChoice> choices = recipe.getChoiceList();
        if (recipe.getResult().isSimilar(new ItemStack(Material.OAK_WOOD))) {
            NeverEnoughRecipes.LOGGER.info(choices.toString());
        }

        for (int i = 0; i < choices.size() && i < TARGET_SLOTS.length; i++) {
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
