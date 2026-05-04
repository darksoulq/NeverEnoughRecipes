package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public record ParsedRecipeView(Map<Integer, List<ItemStack>> slots, Font.TextureGlyph texture, int offset, ItemStack provider) {}