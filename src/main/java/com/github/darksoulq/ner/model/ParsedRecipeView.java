package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;

public record ParsedRecipeView(Map<Integer, List<ItemStack>> slots, Map<ItemStack, String> probabilities, List<PagedSection> pagedSections, Font.TextureGlyph texture, int offset, ItemStack provider) {
    public ParsedRecipeView(Map<Integer, List<ItemStack>> slots, Font.TextureGlyph texture, int offset, ItemStack provider) {
        this(slots, Map.of(), List.of(), texture, offset, provider);
    }

    public static Builder builder(Font.TextureGlyph texture, int offset, ItemStack provider) {
        return new Builder(texture, offset, provider);
    }

    public static class Builder {
        private final Font.TextureGlyph texture;
        private final int offset;
        private final ItemStack provider;
        private final Map<Integer, List<ItemStack>> baseSlots = new HashMap<>();
        private final Map<ItemStack, String> probabilities = new HashMap<>();
        private final List<PagedSection> pagedSections = new ArrayList<>();

        public Builder(Font.TextureGlyph texture, int offset, ItemStack provider) {
            this.texture = texture;
            this.offset = offset;
            this.provider = provider;
        }

        public Builder set(int slot, ItemStack item) {
            if (item != null && !item.isEmpty()) {
                this.baseSlots.put(slot, List.of(item));
            }
            return this;
        }

        public Builder set(int slot, List<ItemStack> items) {
            if (items != null && !items.isEmpty()) {
                this.baseSlots.put(slot, new ArrayList<>(items));
            }
            return this;
        }

        public Builder setChoice(int slot, RecipeChoice choice) {
            if (choice == null || choice.equals(RecipeChoice.empty())) return this;
            if (choice instanceof RecipeChoice.MaterialChoice mat) {
                this.baseSlots.put(slot, mat.getChoices().stream().map(ItemStack::new).toList());
            } else if (choice instanceof RecipeChoice.ExactChoice exact) {
                this.baseSlots.put(slot, new ArrayList<>(exact.getChoices()));
            }
            return this;
        }

        public Builder probability(ItemStack item, String expression) {
            if (item != null && !item.isEmpty() && expression != null && !expression.isBlank()) {
                this.probabilities.put(item, expression);
            }
            return this;
        }

        public Builder probability(ItemStack item, float chance) {
            String formatted = String.valueOf(chance);
            if (formatted.endsWith(".0")) {
                formatted = formatted.substring(0, formatted.length() - 2);
            }
            return probability(item, formatted + "%");
        }

        public Builder probability(List<ItemStack> items, String expression) {
            if (items != null) {
                for (ItemStack item : items) {
                    probability(item, expression);
                }
            }
            return this;
        }

        public Builder probability(List<ItemStack> items, float chance) {
            if (items != null) {
                for (ItemStack item : items) {
                    probability(item, chance);
                }
            }
            return this;
        }

        public Builder addSection(PagedSection section) {
            if (section != null) {
                this.pagedSections.add(section);
            }
            return this;
        }

        public ParsedRecipeView build() {
            return new ParsedRecipeView(baseSlots, probabilities, pagedSections, texture, offset, provider);
        }
    }
}