package com.github.darksoulq.ner.registry;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface CraftabilityChecker<T> {
    boolean canCraft(Player player, T recipe);
}