package com.github.darksoulq.ner.model;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public record VillagerTradeRecipe(MerchantRecipe recipe, ItemStack icon) {}