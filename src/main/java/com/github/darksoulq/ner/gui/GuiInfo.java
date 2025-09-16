package com.github.darksoulq.ner.gui;

import org.bukkit.inventory.ItemStack;

public interface GuiInfo {
    class Main implements GuiInfo {
        public int page;
        public int favouritesPage;
        public MainMenu.FilterType filter;

        public Main(int page, int favouritesPage, MainMenu.FilterType filter) {
            this.page = page;
            this.favouritesPage = favouritesPage;
            this.filter = filter;
        }
    }

    class Search implements GuiInfo {
        public String text;
        public int page;

        public Search(String text, int page) {
            this.text = text;
            this.page = page;
        }
    }

    class Recipe implements GuiInfo {
        public int page;
        public final RecipeViewer.RecipeType type;
        public ItemStack provider;
        public ItemStack viewed;

        public Recipe(int page, RecipeViewer.RecipeType type, ItemStack provider, ItemStack viewed) {
            this.page = page;
            this.type = type;
            this.provider = provider;
            this.viewed = viewed;
        }
    }
}
