package com.github.darksoulq.ner.model;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemModifier {
    ItemStack apply(ItemStack original);
}