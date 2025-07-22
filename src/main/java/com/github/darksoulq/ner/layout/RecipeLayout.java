package com.github.darksoulq.ner.layout;

import com.github.darksoulq.ner.model.ParsedRecipeView;

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
}
