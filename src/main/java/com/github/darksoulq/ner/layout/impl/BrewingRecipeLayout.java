package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import io.papermc.paper.potion.PaperPotionBrewer;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.potion.PotionBrewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrewingRecipeLayout extends RecipeLayout<PotionMix> {
    private static final int[] TARGET_SLOTS = {13, 30, 32, 40};
    @Override
    public Class<PotionMix> getRecipeClass() {
        return PotionMix.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(PotionMix recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        RecipeChoice base = recipe.getIngredient();
        RecipeChoice inputs = recipe.getInput();
        setItems(slotMap, TARGET_SLOTS[0], base);
        if (inputs instanceof RecipeChoice.ExactChoice ie) {
            List<ItemStack> ings = ie.getChoices();
            slotMap.put(TARGET_SLOTS[1], ings);
            slotMap.put(TARGET_SLOTS[2], ings);
            slotMap.put(TARGET_SLOTS[3], ings);
        } else if (inputs instanceof RecipeChoice.MaterialChoice im) {
            List<ItemStack> ings = im.getChoices().stream().map(ItemStack::new).toList();
            slotMap.put(TARGET_SLOTS[1], ings);
            slotMap.put(TARGET_SLOTS[2], ings);
            slotMap.put(TARGET_SLOTS[3], ings);
        }

        return new ParsedRecipeView(slotMap, Pack.BREWING, -8, new ItemStack(Material.BREWING_STAND));
    }
}
