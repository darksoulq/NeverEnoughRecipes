package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmithingTransformRecipeLayout extends RecipeLayout<SmithingTransformRecipe> {
    private static final int[] TARGET_SLOTS = {
            30, 31, 32
    };
    @Override
    public Class<SmithingTransformRecipe> getRecipeClass() {
        return SmithingTransformRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(SmithingTransformRecipe recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        RecipeChoice template = recipe.getTemplate();
        RecipeChoice base =  recipe.getBase();
        RecipeChoice addition = recipe.getAddition();

        setItems(slotMap, TARGET_SLOTS[0], template);
        setItems(slotMap, TARGET_SLOTS[1], base);
        setItems(slotMap, TARGET_SLOTS[2], addition);

        return new ParsedRecipeView(slotMap, Pack.THREE_SLOT, -8, ItemStack.of(Material.SMITHING_TABLE));
    }
}
