package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.TransmuteRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransmuteRecipeLayout extends RecipeLayout<TransmuteRecipe> {
    private static final int[] TARGET_SLOTS = {
            21, 22, 23,
            30, 31, 32,
            39, 40, 41
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

            if (choice instanceof RecipeChoice.ExactChoice exact) {
                slotMap.put(slot, exact.getChoices());
            } else if (choice instanceof RecipeChoice.MaterialChoice mat) {
                slotMap.put(slot, mat.getChoices().stream().map(ItemStack::new).toList());
            }
        }

        ItemStack display = new ItemStack(Material.CRAFTING_TABLE);

        return new ParsedRecipeView(slotMap, Pack.CRAFTING_TABLE, -8, display);
    }
}
