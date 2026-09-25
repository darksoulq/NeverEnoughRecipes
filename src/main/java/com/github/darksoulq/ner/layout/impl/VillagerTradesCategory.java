package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.VillagerTradeRecipe;
import com.github.darksoulq.ner.resources.Pack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;
import java.util.Set;

public class VillagerTradesCategory extends RecipeCategory<VillagerTradeRecipe> {
    @Override
    public Class<VillagerTradeRecipe> getRecipeClass() {
        return VillagerTradeRecipe.class;
    }

    @Override
    public ParsedRecipeView parseRecipe(VillagerTradeRecipe trade, ItemStack catalyst) {
        MerchantRecipe recipe = trade.recipe();
        List<ItemStack> ingredients = recipe.getIngredients();
        ParsedRecipeView.Builder builder = ParsedRecipeView.builder(Pack.TRADING, -8, catalyst);

        if (trade.icon() != null && !trade.icon().isEmpty()) {
            builder.set(13, trade.icon());
        }

        ItemStack adjustedFirst = recipe.getAdjustedIngredient1();
        if (adjustedFirst != null && !adjustedFirst.isEmpty()) {
            builder.set(20, adjustedFirst);
        }

        if (ingredients.size() > 1) {
            ItemStack secondIngredient = ingredients.get(1);
            if (secondIngredient != null && !secondIngredient.isEmpty()) {
                builder.set(21, secondIngredient);
            }
        }

        builder.set(24, recipe.getResult());

        return builder.build();
    }

    @Override
    public Set<Integer> getResultSlots() {
        return Set.of(24);
    }

    @Override
    public Set<Integer> getIgnoredSlots() {
        return Set.of(13);
    }
}