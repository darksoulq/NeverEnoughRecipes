package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;

public class CookingCategory<T extends CookingRecipe> extends RecipeCategory<T> {
    private final Class<T> clazz;

    public CookingCategory(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<T> getRecipeClass() {
        return clazz;
    }

    @Override
    public ParsedRecipeView parseRecipe(T recipe, ItemStack catalyst) {
        Map<Integer, List<ItemStack>> slots = new HashMap<>();
        if (recipe.getInputChoice().equals(RecipeChoice.empty())) {
            return new ParsedRecipeView(slots, Pack.COOKING, 0, catalyst);
        }

        applyChoice(slots, 21, recipe.getInputChoice());
        slots.put(23, List.of(recipe.getResult()));

        Item xpItem = UiItems.XP.clone();
        xpItem.tooltip.addLine(TextUtil.parse("<!italic><green><exp></green>", Placeholder.parsed("exp", String.valueOf(recipe.getExperience()))));
        xpItem.updateTooltip();
        slots.put(32, List.of(xpItem.getStack()));

        return new ParsedRecipeView(slots, Pack.COOKING, -8, catalyst);
    }

    @Override
    public Set<Integer> getResultSlots() { return Set.of(23); }

    @Override
    public Set<Integer> getIgnoredSlots() { return Set.of(32); }
}