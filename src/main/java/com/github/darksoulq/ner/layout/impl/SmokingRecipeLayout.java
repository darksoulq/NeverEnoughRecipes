package com.github.darksoulq.ner.layout.impl;

import org.bukkit.Material;
import org.bukkit.inventory.SmokingRecipe;

public class SmokingRecipeLayout extends AbstractCookingLayout<SmokingRecipe> {
    public SmokingRecipeLayout() {
        super(Material.SMOKER);
    }

    @Override
    public Class<SmokingRecipe> getRecipeClass() {
        return SmokingRecipe.class;
    }
}
