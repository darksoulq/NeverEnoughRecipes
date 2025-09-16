package com.github.darksoulq.ner.layout.impl;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;

public class FurnaceRecipeLayout extends AbstractCookingLayout<FurnaceRecipe> {
    public FurnaceRecipeLayout() {
        super(Material.FURNACE);
    }

    @Override
    public Class<FurnaceRecipe> getRecipeClass() {
        return FurnaceRecipe.class;
    }
}
