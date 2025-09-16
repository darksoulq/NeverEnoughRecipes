package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: Actually load Brewing recipes so it can display (Currently no good way of loading brewing recipes exists)
public abstract class AbstractBrewLayout extends RecipeLayout<PotionMix> {
    private static final int[] TARGET_SLOTS = {12, 30, 23};

    @Override
    public ParsedRecipeView parseRecipe(PotionMix recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        RecipeChoice base = recipe.getIngredient();
        RecipeChoice inputs = recipe.getInput();
        setItems(slotMap, TARGET_SLOTS[0], base);
        setItems(slotMap, TARGET_SLOTS[1], inputs);
        slotMap.put(TARGET_SLOTS[2], List.of(recipe.getResult()));

        return new ParsedRecipeView(slotMap, Pack.BREWING, -8, new ItemStack(Material.BREWING_STAND));
    }

    @Override
    public Set<Integer> getOutputSlots() {
        return Set.of(23);
    }
}
