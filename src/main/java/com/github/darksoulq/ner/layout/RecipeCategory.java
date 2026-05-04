package com.github.darksoulq.ner.layout;

import com.github.darksoulq.ner.model.ParsedRecipeView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RecipeCategory<T> {
    public abstract Class<T> getRecipeClass();
    public abstract ParsedRecipeView parseRecipe(T recipe, ItemStack catalyst);
    public abstract Set<Integer> getResultSlots();
    public abstract Set<Integer> getIgnoredSlots();

    protected void applyChoice(Map<Integer, List<ItemStack>> map, int slot, RecipeChoice choice) {
        if (choice == null || choice.equals(RecipeChoice.empty())) return;
        if (choice instanceof RecipeChoice.MaterialChoice mat) {
            map.put(slot, mat.getChoices().stream().map(ItemStack::new).toList());
        } else if (choice instanceof RecipeChoice.ExactChoice exact) {
            map.put(slot, exact.getChoices());
        }
    }
}