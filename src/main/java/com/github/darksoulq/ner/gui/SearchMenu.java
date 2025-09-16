package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.impl.PaginatedElements;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.ner.data.NamespacedFilterManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TextUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class SearchMenu {
    private static final Map<String, BiFunction<String, ItemStack, Boolean>> FILTERS = new HashMap<>();
    private static final Component MENU_TITLE = TextUtil.parse(
            "<white><offset><title></white><width>Search",
            Placeholder.parsed("title", Pack.SEARCH_MENU.toMiniMessageString()),
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-60)),
            Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))
    );

    private static final int[] DISPLAY_SLOTS = {
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    static {
        addFilter("@", (query, stack) -> {
            List<String> namespaces = NamespacedFilterManager.getMatchingNamespaces(query);
            return NamespacedFilterManager.getItems(namespaces).contains(stack);
        });
    }

    public static void addFilter(String key, BiFunction<String, ItemStack, Boolean> filter) {
        FILTERS.put(key.toLowerCase(Locale.ROOT), filter);
    }

    public static Gui create() {
        return create(new GuiInfo.Search("", 0));
    }

    public static Gui create(GuiInfo.Search info) {
        List<GuiElement> guiElements = new ArrayList<>(MainMenu.ITEMS.size());
        MainMenu.populateElements(guiElements, info);

        PaginatedElements paginatedElements = new PaginatedElements(guiElements, DISPLAY_SLOTS, GuiView.Segment.BOTTOM);

        Predicate<GuiElement> initialFilter = buildFilterPredicate(info.text, null);
        paginatedElements.setFilter(initialFilter);

        ItemStack invisibleFiller = Items.INVISIBLE_ITEM.get().getStack();
        invisibleFiller.setData(DataComponentTypes.ITEM_NAME, Component.text(""));
        invisibleFiller.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());

        ItemStack pageIndicator = MainMenu.createPageIndicator();
        MainMenu.updatePageIndicator(paginatedElements, pageIndicator, info);

        return new Gui.Builder(MenuType.ANVIL, MENU_TITLE)
                .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
                .addLayer(paginatedElements)
                .set(SlotPosition.bottom(0), new GuiButton(UiItems.PREV.get().getStack(), (view, click) -> {
                    paginatedElements.prev(view);
                    MainMenu.updatePageIndicator(paginatedElements, pageIndicator, info);
                }))
                .set(SlotPosition.bottom(4), new GuiItem(pageIndicator))
                .set(SlotPosition.bottom(8), new GuiButton(UiItems.NEXT.get().getStack(), (view, click) -> {
                    paginatedElements.next(view);
                    MainMenu.updatePageIndicator(paginatedElements, pageIndicator, info);
                }))
                .set(SlotPosition.top(0), new GuiItem(invisibleFiller))
                .onOpen(MainMenu::setupBackup)
                .onTick(view -> {
                    if (!(view.getInventoryView() instanceof AnvilView anvilView)) {
                        return;
                    }

                    String currentInput = Optional.ofNullable(anvilView.getRenameText()).orElse("");
                    boolean isOld = info.text.equals(currentInput);

                    if (isOld) return;
                    info.text = currentInput;

                    Predicate<GuiElement> filterPredicate = buildFilterPredicate(info.text, view);
                    paginatedElements.setFilter(filterPredicate);
                })
                .onClose(MainMenu::loadBackup)
                .build();
    }

    private static Predicate<GuiElement> buildFilterPredicate(String input, GuiView view) {
        int colonIndex = input.indexOf(':');
        if (colonIndex > 0) {
            String prefix = input.substring(0, colonIndex).toLowerCase(Locale.ROOT);
            String searchTerm = input.substring(colonIndex + 1);

            BiFunction<String, ItemStack, Boolean> filterFunc = FILTERS.get(prefix);
            if (filterFunc != null) {
                return element -> {
                    ItemStack item = element.render(view, 0);
                    boolean result = filterFunc.apply(searchTerm, item);
                    return result;
                };
            }
            return fallbackFilter(input);
        }
        return fallbackFilter(input.toLowerCase(Locale.ROOT));
    }
    private static Predicate<GuiElement> fallbackFilter(String query) {
        String loweredQuery = query.toLowerCase(Locale.ROOT);
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();

        return element -> {
            ItemStack item = element.render(null, 0);
            if (item == null) {
                return false;
            }

            Component name = item.getData(DataComponentTypes.CUSTOM_NAME);
            if (name == null) name = item.getData(DataComponentTypes.ITEM_NAME);
            if (name == null) {
                return false;
            }

            String plainName = plainSerializer.serialize(name).toLowerCase(Locale.ROOT);
            boolean matches = plainName.contains(loweredQuery);
            return matches;
        };
    }
}
