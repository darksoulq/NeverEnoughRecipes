package com.github.darksoulq.ner.model;

import org.bukkit.inventory.ItemStack;

public interface GuiEntry {
    record ItemEntry(ItemStack item) implements GuiEntry {}
    record GroupEntry(ItemGroup group) implements GuiEntry {}
    record GroupCollapseEntry(ItemGroup group) implements GuiEntry {}
}