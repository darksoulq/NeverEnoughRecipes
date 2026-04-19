package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.layer.LayerStack;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.ner.data.RecipeManager;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.RecipeViewLayer;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TaskUtil;
import com.github.darksoulq.ner.util.TextUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class RecipeViewer {
    public static Gui create(ItemStack stack, int page, RecipeType type) {
        return create(stack, page, type, null);
    }

    public static Gui create(ItemStack stack, int page, RecipeType type, ItemStack provided) {
        List<Object> recipes = switch (type) {
            case RECIPE -> RecipeManager.getRecipesForResult(stack);
            case USE -> RecipeManager.getUsesForItem(stack);
        };
        if (recipes.isEmpty()) {
            throw new IllegalStateException("No recipes found for " + stack);
        }

        Map<ItemStack, List<RecipeViewLayer>> layers = new LinkedHashMap<>();

        GuiInfo.Recipe info = new GuiInfo.Recipe(page, type, null, stack);

        for (Object recipe : recipes) {
            ParsedRecipeView view = RecipeManager.parse(recipe);
            ItemStack provider = view.getProvider();
            layers.computeIfAbsent(provider, k -> new ArrayList<>())
                .add(new RecipeViewLayer(view, info));
        }

        if (layers.isEmpty()) {
            throw new IllegalStateException("No layers parsed from recipes for " + stack);
        }

        ItemStack provider = provided == null ? layers.keySet().iterator().next() : provided;
        info.provider = provider;

        Supplier<@NotNull Component> itemName = () -> {
            if (stack.hasData(DataComponentTypes.CUSTOM_NAME))
                return stack.getData(DataComponentTypes.CUSTOM_NAME);
            if (stack.hasData(DataComponentTypes.ITEM_NAME))
                return stack.getData(DataComponentTypes.ITEM_NAME);
            if (stack.getItemMeta().hasDisplayName()) return stack.displayName();
            return Component.text("");
        };

        ParsedRecipeView currentView = layers.get(provider).getFirst().view;
        Component title = TextUtil.parse("<white><offset><title></white><width><type> of <itemname>",
            Placeholder.parsed("title", currentView.getTexture().toMiniMessageString()),
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(currentView.getOffset())),
            Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-168)),
            Placeholder.parsed("type", type.equals(RecipeType.USE) ? "Uses" : "Recipes"),
            Placeholder.component("itemname", itemName.get()));
        return create(title, layers, info);
    }

    public static Gui create(Component title, Map<ItemStack, List<RecipeViewLayer>> layers, GuiInfo.Recipe info) {
        LayerStack layerStack = new LayerStack(new ArrayList<>(layers.get(info.provider)));

        List<GuiElement> providerButtons = new ArrayList<>(layers.keySet().stream()
            .map(k -> {
                ItemStack display = k.clone();
                if (k.equals(info.provider)) {
                    display.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                }
                return new GuiButton(display, (ctx) -> {
                    ParsedRecipeView rview = layers.get(k).getFirst().view;
                    Supplier<@NotNull Component> itemName = () -> {
                        if (info.viewed.hasData(DataComponentTypes.CUSTOM_NAME))
                            return info.viewed.getData(DataComponentTypes.CUSTOM_NAME);
                        if (info.viewed.hasData(DataComponentTypes.ITEM_NAME))
                            return info.viewed.getData(DataComponentTypes.ITEM_NAME);
                        if (info.viewed.getItemMeta().hasDisplayName()) return info.viewed.displayName();
                        return Component.text("");
                    };
                    Component newTitle = TextUtil.parse("<white><offset><title></white><width><type> of <itemname>",
                        Placeholder.parsed("title", rview.getTexture().toMiniMessageString()),
                        Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(rview.getOffset())),
                        Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-168)),
                        Placeholder.parsed("type", info.type.equals(RecipeType.USE) ? "Uses" : "Recipes"),
                        Placeholder.component("itemname", itemName.get()));
                    GuiManager.remove(ctx.view());
                    Gui gui = create(newTitle, layers, new GuiInfo.Recipe(info.page, info.type, k, info.viewed));
                    GuiManager.open(ctx.view().getPlayer(), gui);
                });
            })
            .toList());

        PagedLayer<GuiElement> paginatedProviders = PagedLayer.of(providerButtons,
            new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35},
            GuiView.Segment.BOTTOM);

        ItemStack pageDisplay = MainMenu.createPageIndicator();
        updatePageItem(layerStack, pageDisplay, info);

        return new Gui.Builder(MenuType.GENERIC_9X6, title)
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .addLayer(layerStack)
            .addLayer(paginatedProviders)
            .set(SlotPosition.bottom(0), GuiButton.of(
                UiItems.PREV.getStack(),
                (ctx) -> {
                    paginatedProviders.previous(ctx.view());
                    paginatedProviders.renderTo(ctx.view());
                }))
            .set(SlotPosition.bottom(8), GuiButton.of(
                UiItems.NEXT.getStack(),
                (ctx) -> {
                    paginatedProviders.next(ctx.view());
                    paginatedProviders.renderTo(ctx.view());
                }))
            .set(SlotPosition.top(48), GuiButton.of(
                UiItems.PREV.getStack(),
                (ctx) -> {
                    if (layerStack.size() > 1) {
                        layerStack.previous(ctx.view());
                        layerStack.renderTo(ctx.view());
                    }
                    updatePageItem(layerStack, pageDisplay, info);
                }))
            .set(SlotPosition.top(49), GuiItem.of(pageDisplay))
            .set(SlotPosition.top(53), GuiButton.of(
                UiItems.CLOSE.getStack(),
                (ctx) -> {
                    Player player = ctx.view().getPlayer();
                    GuiInfo prev = GuiHistory.pop(player.getUniqueId());
                    switch (prev) {
                        case GuiInfo.Main m -> {
                            GuiManager.remove(ctx.view());
                            GuiManager.open(player, MainMenu.create(m));
                        }
                        case GuiInfo.Recipe r -> {
                            GuiManager.remove(ctx.view());
                            GuiManager.open(player, create(r.viewed, r.page, r.type, r.provider));
                        }
                        case null, default -> {
                            GuiManager.close(player);
                        }
                    }
                }))
            .set(SlotPosition.top(50), GuiButton.of(
                UiItems.NEXT.getStack(),
                (ctx) -> {
                    if (layerStack.size() > 1) {
                        layerStack.next(ctx.view());
                        layerStack.renderTo(ctx.view());
                    }
                    updatePageItem(layerStack, pageDisplay, info);
                }))
            .onOpen(view -> {
                MainMenu.setupBackup(view);
                TaskUtil.delayedTask(1, () -> {
                    layerStack.setIndex(view, info.page);
                    layerStack.renderTo(view);
                    updatePageItem(layerStack, pageDisplay, info);
                });
            })
            .onClose(view -> {
                view.getBottom().clear();
                view.getTop().clear();
                if (!GuiManager.OPEN_VIEWS.containsKey(view.getInventoryView())) {
                    MainMenu.loadBackup(view);
                }
            })
            .build();
    }

    private static void updatePageItem(LayerStack layer, ItemStack stack, GuiInfo.Recipe info) {
        String pageLore = String.format("<green>%d <gray>of</gray> %d</green>",
            layer.getIndex() + 1, Math.max(1, layer.size()));
        stack.setData(DataComponentTypes.LORE, ItemLore.lore()
            .lines(List.of(TextUtil.parse(pageLore)))
            .build());
        info.page = layer.getIndex();
    }

    public enum RecipeType {
        RECIPE, USE
    }
}