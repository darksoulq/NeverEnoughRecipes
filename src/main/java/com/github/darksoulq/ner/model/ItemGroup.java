package com.github.darksoulq.ner.model;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record ItemGroup(String id, Component title, List<ItemStack> items, boolean animate) {}