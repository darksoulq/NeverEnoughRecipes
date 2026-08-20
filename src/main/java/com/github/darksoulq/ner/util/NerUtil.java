package com.github.darksoulq.ner.util;

import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.ner.gui.InventoryBackupManager;
import com.github.darksoulq.ner.gui.RecipeViewer;
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.RecipeStage;
import com.github.darksoulq.ner.registry.RecipeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

public final class NerUtil {
    private NerUtil() {}

    public static void openRecipeView(Player player, ItemStack item) {
        if (item == null || item.isEmpty()) return;
        if (GuiManager.OPEN_VIEWS.containsKey(player.getOpenInventory())) {
            InventoryBackupManager.transition(GuiManager.OPEN_VIEWS.get(player.getOpenInventory()));
        }
        GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.RECIPE));
    }

    public static void openUsesView(Player player, ItemStack item) {
        if (item == null || item.isEmpty()) return;
        if (GuiManager.OPEN_VIEWS.containsKey(player.getOpenInventory())) {
            InventoryBackupManager.transition(GuiManager.OPEN_VIEWS.get(player.getOpenInventory()));
        }
        GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.USE));
    }

    public record RecipeData(ParsedRecipeView view, Object rawRecipe, RecipeCategory<?> category, ItemStack catalyst, Map<Integer, List<ItemStack>> inputs, Map<Integer, List<ItemStack>> outputs) {
        public boolean isCatalyst(Material material) {
            return catalyst != null && catalyst.getType() == material;
        }

        public boolean isCatalyst(Predicate<ItemStack> filter) {
            return catalyst != null && filter.test(catalyst);
        }

        public boolean isRecipeClass(Class<?> recipeClass) {
            return rawRecipe != null && recipeClass.isAssignableFrom(rawRecipe.getClass());
        }

        public List<ItemStack> getInputsAt(int slot) {
            return inputs.getOrDefault(slot, Collections.emptyList());
        }

        public List<ItemStack> getOutputsAt(int slot) {
            return outputs.getOrDefault(slot, Collections.emptyList());
        }

        public List<ItemStack> getPrimaryOutputs() {
            List<ItemStack> list = new ArrayList<>();
            for (List<ItemStack> items : outputs.values()) {
                if (items != null && !items.isEmpty()) {
                    list.add(items.getFirst());
                }
            }
            return list;
        }
        public ItemStack getPrimaryResult() {
            List<ItemStack> primary = getPrimaryOutputs();
            return primary.isEmpty() ? null : primary.getFirst();
        }

        public boolean isCraftable(Player player) {
            return RecipeManager.isCraftable(player, view);
        }
    }

    public static RecipeData createData(ParsedRecipeView view) {
        if (view == null) return null;
        Object raw = RecipeManager.getRawRecipe(view);
        RecipeCategory<?> cat = RecipeManager.getCategory(view);

        Set<Integer> resultSlots = cat != null ? cat.getResultSlots() : Collections.emptySet();
        Set<Integer> ignoredSlots = cat != null ? cat.getIgnoredSlots() : Collections.emptySet();

        Map<Integer, List<ItemStack>> inputs = new HashMap<>();
        Map<Integer, List<ItemStack>> outputs = new HashMap<>();

        for (RecipeStage stage : view.stages()) {
            for (Map.Entry<Integer, List<ItemStack>> entry : stage.slots().entrySet()) {
                int slot = entry.getKey();
                if (ignoredSlots.contains(slot)) continue;
                if (resultSlots.contains(slot)) {
                    outputs.put(slot, entry.getValue());
                } else {
                    inputs.put(slot, entry.getValue());
                }
            }
        }

        return new RecipeData(view, raw, cat, view.provider(), inputs, outputs);
    }

    public static List<RecipeData> getAllRecipes() {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getAllRecipes()) {
            RecipeData data = createData(view);
            if (data != null) list.add(data);
        }
        return list;
    }

    public static List<RecipeData> getRecipesByType(Class<?> recipeClass) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getAllRecipes()) {
            Object raw = RecipeManager.getRawRecipe(view);
            if (raw != null && recipeClass.isAssignableFrom(raw.getClass())) {
                RecipeData data = createData(view);
                if (data != null) list.add(data);
            }
        }
        return list;
    }

    public static List<RecipeData> getRecipesByType(Class<?> recipeClass, Predicate<ItemStack> catalystFilter) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getAllRecipes()) {
            Object raw = RecipeManager.getRawRecipe(view);
            if (raw != null && recipeClass.isAssignableFrom(raw.getClass()) && catalystFilter.test(view.provider())) {
                RecipeData data = createData(view);
                if (data != null) list.add(data);
            }
        }
        return list;
    }

    public static List<RecipeData> getRecipesByCatalyst(ItemStack item) {
        return getRecipesByCatalyst(catalyst -> catalyst != null && catalyst.isSimilar(item));
    }

    public static List<RecipeData> getRecipesByCatalyst(Predicate<ItemStack> catalystFilter) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getAllRecipes()) {
            if (catalystFilter.test(view.provider())) {
                RecipeData data = createData(view);
                if (data != null) list.add(data);
            }
        }
        return list;
    }

    public static List<RecipeData> getRecipesForOutput(ItemStack output) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getRecipes(output)) {
            RecipeData data = createData(view);
            if (data != null) list.add(data);
        }
        return list;
    }

    public static List<RecipeData> getRecipesForOutput(ItemStack output, Predicate<ItemStack> catalystFilter) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getRecipes(output)) {
            if (catalystFilter.test(view.provider())) {
                RecipeData data = createData(view);
                if (data != null) list.add(data);
            }
        }
        return list;
    }

    public static List<RecipeData> getUsesForInput(ItemStack input) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getUses(input)) {
            RecipeData data = createData(view);
            if (data != null) list.add(data);
        }
        return list;
    }

    public static List<RecipeData> getUsesForInput(ItemStack input, Predicate<ItemStack> catalystFilter) {
        List<RecipeData> list = new ArrayList<>();
        for (ParsedRecipeView view : RecipeManager.getUses(input)) {
            if (catalystFilter.test(view.provider())) {
                RecipeData data = createData(view);
                if (data != null) list.add(data);
            }
        }
        return list;
    }
}