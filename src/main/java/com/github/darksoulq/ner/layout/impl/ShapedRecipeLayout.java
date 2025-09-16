package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class ShapedRecipeLayout extends RecipeLayout<ShapedRecipe> {

    private static final int[] TARGET_SLOTS = {
            11, 12, 13,
            20, 21, 22,
            29, 30, 31
    };

    @Override
    public Class<ShapedRecipe> getRecipeClass() {
        return ShapedRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(ShapedRecipe recipe) {
        String[] shape = recipe.getShape();
        Map<Character, RecipeChoice> ing = recipe.getChoiceMap();

        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();

        String[] normalizedShape = new String[3];
        Arrays.fill(normalizedShape, "   ");

        for (int row = 0; row < shape.length; row++) {
            String line = shape[row];
            normalizedShape[row] = String.format("%-3s", line);
        }

        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++, index++) {
                char key = normalizedShape[row].charAt(col);
                RecipeChoice choice = ing.get(key);
                if (choice == null || choice.equals(RecipeChoice.empty())) continue;

                setItems(slotMap, TARGET_SLOTS[index], choice);
            }
        }

        slotMap.put(24, List.of(recipe.getResult()));
        return new ParsedRecipeView(slotMap, Pack.CRAFTING_TABLE, -8, new ItemStack(Material.CRAFTING_TABLE));
    }

    @Override
    public Set<Integer> getOutputSlots() {
        return Set.of(24);
    }
}
