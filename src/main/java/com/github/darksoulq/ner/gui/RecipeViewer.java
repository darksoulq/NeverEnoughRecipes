package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.*;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.GuiButton;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.ListedLayers;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.PaginatedElements;
import com.github.darksoulq.abyssallib.world.level.item.Items;
import com.github.darksoulq.ner.RecipeManager;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.RecipeViewLayer;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TextUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.*;

public class RecipeViewer {

    public static Gui create(ItemStack stack) {
        List<Object> recipes = RecipeManager.getRecipesForResult(stack);
        if (recipes.isEmpty()) {
            throw new IllegalStateException("No recipes found for " + stack);
        }

        Map<ItemStack, List<RecipeViewLayer>> layers = new LinkedHashMap<>();

        for (Object recipe : recipes) {
            ParsedRecipeView view = RecipeManager.parse(recipe);
            ItemStack provider = view.getProvider();
            layers.computeIfAbsent(provider, k -> new ArrayList<>())
                    .add(new RecipeViewLayer(view));
        }

        if (layers.isEmpty()) {
            throw new IllegalStateException("No layers parsed from recipes for " + stack);
        }

        // First provider for initial view
        ItemStack provider = layers.keySet().iterator().next();

        ParsedRecipeView currentView = layers.get(provider).getFirst().view;
        Component title = TextUtil.parse("<white><offset><title></white>",
                Placeholder.parsed("title", currentView.getTexture().toMiniMessageString()),
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(currentView.getOffset())));
        return create(provider, title, layers);
    }

    public static Gui create(ItemStack provider, Component title, Map<ItemStack, List<RecipeViewLayer>> layers) {
        ListedLayers listedLayers = new ListedLayers(new ArrayList<>(layers.get(provider)));

        List<GuiElement> providerButtons = new ArrayList<>(layers.keySet().stream()
                .map(k -> {
                    ItemStack display = k.clone();
                    if (k.equals(provider)) {
                        display.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                    }
                    return new GuiButton(display, (view, type) -> {
                        ParsedRecipeView rview = layers.get(k).getFirst().view;
                        Component newTitle = TextUtil.parse("<white><offset><title></white>",
                                Placeholder.parsed("title", rview.getTexture().toMiniMessageString()),
                                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(rview.getOffset())));
                        Gui gui = create(k, newTitle, layers);
                        GuiManager.open(view.getInventoryView().getPlayer(), gui);
                    });
                })
                .toList());

        PaginatedElements paginatedProviders = new PaginatedElements(providerButtons,
                new int[]{1, 2, 3, 4, 5, 6, 7}, GuiView.Segment.TOP);

        return new Gui.Builder(MenuType.GENERIC_9X6, title)
                .addFlags(GuiFlag.DISABLE_ADVANCEMENTS)
                .addLayer(listedLayers)
                .addLayer(paginatedProviders)
                .set(SlotPosition.top(0), GuiButton.of(
                        UiItems.PREV.get().getStack(),
                        (view, type) -> paginatedProviders.prev(view)))
                .set(SlotPosition.top(8), GuiButton.of(
                        UiItems.NEXT.get().getStack(),
                        (view, type) -> paginatedProviders.next(view)))
                .set(SlotPosition.top(45), GuiButton.of(
                        UiItems.PREV.get().getStack(),
                        (view, type) -> {
                            Inventory top = view.getTop();
                            ItemStack[] reset = top.getContents();
                            Arrays.fill(reset, null);
                            top.setContents(reset);
                            listedLayers.prev(view);
                        }))
                .set(SlotPosition.top(49), GuiButton.of(
                        UiItems.CLOSE.get().getStack(),
                        (view, type) ->
                                GuiManager.open(view.getInventoryView().getPlayer(), MainMenu.create())))
                .set(SlotPosition.top(53), GuiButton.of(
                        UiItems.NEXT.get().getStack(),
                        (view, type) -> listedLayers.next(view)))
                .build();
    }
}
