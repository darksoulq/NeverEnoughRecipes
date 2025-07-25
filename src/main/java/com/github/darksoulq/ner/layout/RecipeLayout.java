package com.github.darksoulq.ner.layout;

import com.github.darksoulq.ner.model.ParsedRecipeView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.Map;

/**
 * One implementation per recipe‑class type.
 * @param <T> the concrete recipe class this layout knows how to parse
 */
public abstract class RecipeLayout<T> {

    /** The recipe class this layout supports */
    public abstract Class<T> getRecipeClass();

    /**
     * Convert a recipe instance to a slot → item(s) map, ready for GUI.
     * Implementations must shift indices by +9 to skip the reserved top row.
     */
    public abstract ParsedRecipeView parseRecipe(T recipe);

    public static void setItems(Map<Integer, List<ItemStack>> slotMap, int slot, RecipeChoice choice) {
        if (choice == null || choice.equals(RecipeChoice.empty())) return;
        if (choice instanceof RecipeChoice.MaterialChoice material) {
            slotMap.put(slot, material.getChoices().stream().map(ItemStack::new).toList());
        } else if (choice instanceof RecipeChoice.ExactChoice exact) {
            slotMap.put(slot, exact.getChoices());
        }
    }
}
