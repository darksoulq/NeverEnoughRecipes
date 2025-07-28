package com.github.darksoulq.ner;

import com.github.darksoulq.ner.data.NamespacedFilterManager;
import com.github.darksoulq.ner.data.RecipeManager;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.layout.RecipeLayoutRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public class NerApi {
    public static void addItem(ItemStack item) {
        RecipeManager.addItem(item.asOne());
    }

    public static void registerRecipe(ItemStack result, Object recipe) {
        RecipeManager.addCustomRecipe(recipe, result.asOne());
    }

    public static void addItemToNamespace(String namespace, ItemStack item) {
        NamespacedFilterManager.addItem(namespace, item.asOne());
    }

    public static <T> void registerLayout(RecipeLayout<T> layout) {
        RecipeLayoutRegistry.register(layout);
    }

    public static void ignoreVanillaRecipe(NamespacedKey key) {
        RecipeManager.addIgnoredRecipe(key);
    }

    public static void registerMenuFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter) {
        MainMenu.addFilter(prefix, filter);
    }
}
