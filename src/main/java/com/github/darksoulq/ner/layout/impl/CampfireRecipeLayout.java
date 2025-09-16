package com.github.darksoulq.ner.layout.impl;

import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;

public class CampfireRecipeLayout extends AbstractCookingLayout<CampfireRecipe> {
    public CampfireRecipeLayout() {
        super(Material.CAMPFIRE);
    }

    @Override
    public Class<CampfireRecipe> getRecipeClass() {
        return CampfireRecipe.class;
    }
}
