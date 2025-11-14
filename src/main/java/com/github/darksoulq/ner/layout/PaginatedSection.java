package com.github.darksoulq.ner.layout;

import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class PaginatedSection {
    private final int[] slots;
    private final List<ItemStack> stacks;
    private final Button nextButton, prevButton;

    public PaginatedSection(int[] slots, List<ItemStack> stacks, Button nextButton, Button prevButton) {
        this.slots = slots;
        this.stacks = stacks;
        this.nextButton = nextButton;
        this.prevButton = prevButton;
    }

    public int[] getSlots() {
        return slots;
    }
    public List<ItemStack> getStacks() {
        return stacks;
    }
    public Button getNextButton() {
        return nextButton;
    }
    public Button getPrevButton() {
        return prevButton;
    }

    public record Button(ItemStack display, int slot) {}
}
