package com.github.darksoulq.ner.layout.impl;

import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TextUtil;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCookingLayout<T extends CookingRecipe> extends RecipeLayout<T> {
    private static final int[] TARGET_SLOTS = new int[] {21, 23, 32};
    private final Material provider;

    public AbstractCookingLayout(Material provider) {
        this.provider = provider;
    }

    @Override
    public ParsedRecipeView parseRecipe(T recipe) {
        Map<Integer, List<ItemStack>> slotMap = new HashMap<>();
        RecipeChoice ingredient = recipe.getInputChoice();
        ItemStack output = recipe.getResult();
        float exp = recipe.getExperience();
        if (ingredient.equals(RecipeChoice.empty()))
            return new ParsedRecipeView(new HashMap<>(), Pack.COOKING, 0, new ItemStack(provider));
        setItems(slotMap, TARGET_SLOTS[0], ingredient);
        slotMap.put(TARGET_SLOTS[1], List.of(output));
        Item expItem = UiItems.XP.get().clone();
        expItem.tooltip.addLine(TextUtil.parse("<green><exp></green>",
                        Placeholder.parsed("exp", String.valueOf(recipe.getExperience()))
                        ).decoration(TextDecoration.ITALIC, false));
        expItem.updateTooltip();
        slotMap.put(TARGET_SLOTS[2], List.of(expItem.getStack().clone()));
        return new ParsedRecipeView(slotMap, Pack.COOKING, -8, new ItemStack(provider));
    }

    @Override
    public Set<Integer> getOutputSlots() {
        return Set.of(23);
    }
}
