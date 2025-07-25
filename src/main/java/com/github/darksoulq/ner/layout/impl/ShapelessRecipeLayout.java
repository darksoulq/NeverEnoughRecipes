package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapelessRecipeLayout extends RecipeLayout<ShapelessRecipe> {
    private static final int[] TARGET_SLOTS = {
            21, 22, 23,
            30, 31, 32,
            39, 40, 41
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

        ItemStack display = new ItemStack(Material.CRAFTING_TABLE);

        return new ParsedRecipeView(slotMap, Pack.CRAFTING_TABLE, -8, display);
    }
}
