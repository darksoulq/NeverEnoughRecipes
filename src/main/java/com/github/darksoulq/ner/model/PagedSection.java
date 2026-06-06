package com.github.darksoulq.ner.model;

import org.bukkit.inventory.ItemStack;
import java.util.List;

public record PagedSection(int[] slots, List<ItemStack> items, SectionButton prevButton, SectionButton nextButton) {}