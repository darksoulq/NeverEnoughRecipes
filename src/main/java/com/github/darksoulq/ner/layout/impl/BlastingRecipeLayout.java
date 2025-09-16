package com.github.darksoulq.ner.layout.impl;

import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;

public class BlastingRecipeLayout extends AbstractCookingLayout<BlastingRecipe> {
    public BlastingRecipeLayout() {
        super(Material.BLAST_FURNACE);
    }

    @Override
    public Class<BlastingRecipe> getRecipeClass() {
        return BlastingRecipe.class;
    }
}
