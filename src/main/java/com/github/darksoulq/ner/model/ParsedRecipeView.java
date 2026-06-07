package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public record ParsedRecipeView(List<RecipeStage> stages, ItemStack provider) {

    public static Builder builder(Font.TextureGlyph texture, int offset, ItemStack provider) {
        return new Builder(texture, offset, provider);
    }

    public static class Builder {
        private final ItemStack provider;
        private final RecipeStage.Builder primaryStage;
        private final List<RecipeStage> extraStages = new ArrayList<>();

        public Builder(Font.TextureGlyph texture, int offset, ItemStack provider) {
            this.provider = provider;
            this.primaryStage = RecipeStage.builder(texture, offset);
        }

        public Builder set(int slot, ItemStack item) {
            primaryStage.set(slot, item);
            return this;
        }

        public Builder set(int slot, List<ItemStack> items) {
            primaryStage.set(slot, items);
            return this;
        }

        public Builder setChoice(int slot, RecipeChoice choice) {
            primaryStage.setChoice(slot, choice);
            return this;
        }

        public Builder probability(ItemStack item, String expression) {
            primaryStage.probability(item, expression);
            return this;
        }

        public Builder probability(ItemStack item, float chance) {
            primaryStage.probability(item, chance);
            return this;
        }

        public Builder probability(List<ItemStack> items, String expression) {
            primaryStage.probability(items, expression);
            return this;
        }

        public Builder probability(List<ItemStack> items, float chance) {
            primaryStage.probability(items, chance);
            return this;
        }

        public Builder addSection(PagedSection section) {
            primaryStage.addSection(section);
            return this;
        }

        public Builder addStage(RecipeStage stage) {
            if (stage != null) {
                this.extraStages.add(stage);
            }
            return this;
        }

        public ParsedRecipeView build() {
            List<RecipeStage> allStages = new ArrayList<>();
            allStages.add(primaryStage.build());
            allStages.addAll(extraStages);
            return new ParsedRecipeView(allStages, provider);
        }
    }
}