package com.github.darksoulq.ner;

import com.github.darksoulq.ner.layout.RecipeLayout;
import com.github.darksoulq.ner.layout.RecipeLayoutRegistry;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import io.papermc.paper.potion.PaperPotionBrewer;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;

public class RecipeManager {
    private static final Map<ItemStack, List<Object>> recipeMap = new HashMap<>();

    public static void loadVanillaRecipes() {
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (RecipeLayoutRegistry.hasLayout(recipe.getClass())) {
                recipeMap.computeIfAbsent(recipe.getResult().asOne(), k -> new ArrayList<>()).add(recipe);
            }
        });
    }

    public static void addCustomRecipe(Object recipe, ItemStack result) {
        if (!RecipeLayoutRegistry.hasLayout(recipe.getClass())) {
            throw new IllegalArgumentException(
                    "No RecipeLayout registered for: " + recipe.getClass().getName());
        }
        recipeMap.computeIfAbsent(result, k -> new ArrayList<>()).add(recipe);
    }

    public static Set<ItemStack> getAllItems() {
        return recipeMap.keySet();
    }

    public static ParsedRecipeView parse(Object recipe) {
        RecipeLayout layout = RecipeLayoutRegistry.getLayout(recipe.getClass());
        if (layout == null) {
            throw new IllegalStateException(
                    "No layout for class: " + recipe.getClass().getName());
        }
        return ((RecipeLayout<Object>) layout).parseRecipe(recipe);
    }

    public static List<Object> getRecipesForResult(ItemStack result) {
        return recipeMap.getOrDefault(result, List.of());
    }
}
