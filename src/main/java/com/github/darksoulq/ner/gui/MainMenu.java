package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.impl.PaginatedElements;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.data.Input;
import com.github.darksoulq.ner.data.NamespacedFilterManager;
import com.github.darksoulq.ner.data.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TaskUtil;
import com.github.darksoulq.ner.util.TextUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class MainMenu {
    static final Map<UUID, ItemStack[]> INVENTORY_BACKUPS = new HashMap<>();
    public static List<ItemStack> ITEMS = sortDisplay(new ArrayList<>(RecipeManager.getAllItems()));

    private static Map<FilterType, ItemStack> FILTER_ITEMS;
    private static final int[] DISPLAY_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final int[] BOTTOM_SLOTS = {
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    public static void init() {
        FILTER_ITEMS = Map.of(
                FilterType.RECENT, createFilterItem(FilterType.RECENT),
                FilterType.INVENTORY, createFilterItem(FilterType.INVENTORY),
                FilterType.FAVOURITE, createFilterItem(FilterType.FAVOURITE)
        );
    }

    public static Gui create() {
        return create(new GuiInfo.Main(0, 0, FilterType.RECENT));
    }
    public static Gui create(GuiInfo.Main info) {
        List<GuiElement> displayElements = new ArrayList<>();
        populateElements(displayElements, info);
        PaginatedElements displayPage = new PaginatedElements(displayElements, DISPLAY_SLOTS, GuiView.Segment.TOP);

        Function<GuiView, List<GuiElement>> getFilterElements = view -> {
            UUID uuid = view.getInventoryView().getPlayer().getUniqueId();
            List<GuiElement> elements = new ArrayList<>();

            if (info.filter == FilterType.INVENTORY) {
                ItemStack[] items = INVENTORY_BACKUPS.get(uuid);
                if (items == null) return elements;
                items = items.clone();
                for (ItemStack item : items) {
                    if (item == null) {
                        elements.add(GuiItem.of(ItemStack.empty()));
                        continue;
                    }
                    elements.add(GuiButton.of(item, (gv, click) -> {
                        if (!click.isShiftClick() && click.isLeftClick()) {
                            openRecipe(view, item, info, RecipeViewer.RecipeType.RECIPE);
                        } else if (click.isRightClick()) {
                            openRecipe(view, item, info, RecipeViewer.RecipeType.USE);
                        } else if (Input.isShiftLeftClick(click)) {
                            addFavourite(view.getInventoryView().getPlayer().getUniqueId(), item);
                        }
                    }));
                }
                return elements;
            }

            if (info.filter == FilterType.FAVOURITE) {
                List<ItemStack> favourites = NeverEnoughRecipes.getPrefs().favourites.get().get(uuid.toString());
                if (favourites == null) return elements;
                for (ItemStack item : favourites) {
                    elements.add(GuiButton.of(item, (gv, click) -> {
                        if (Input.isShiftRightClick(click)) {
                            removeFavourite(uuid, item);
                        } else if (click.isLeftClick()) {
                            openRecipe(view, item, info, RecipeViewer.RecipeType.RECIPE);
                        } else if (!click.isShiftClick() && click.isRightClick()) {
                            openRecipe(view, item, info, RecipeViewer.RecipeType.USE);
                        }
                    }));
                }
                return elements;
            }

            if (info.filter == FilterType.RECENT) {
                List<ItemStack> recents = NeverEnoughRecipes.getPrefs().recents.get().get(uuid.toString());
                if (recents == null) return elements;
                for (ItemStack item : recents) {
                    elements.add(GuiButton.of(item, (gv, click) -> {
                        if (!click.isShiftClick() && click.isLeftClick()) {
                            openRecipe(gv, item, info, RecipeViewer.RecipeType.RECIPE);
                        } else if (click.isRightClick()) {
                            openRecipe(gv, item, info, RecipeViewer.RecipeType.USE);
                        } else if (Input.isShiftLeftClick(click)) {
                            addFavourite(uuid, item);
                        }
                    }));
                }
            }

            return elements;
        };
        AtomicReference<PaginatedElements> filterPage = new AtomicReference<>();

        ItemStack displayPageIndicator = createPageIndicator();
        updatePageIndicator(displayPage, displayPageIndicator, info);

        Gui.Builder gui = new Gui.Builder(MenuType.GENERIC_9X6, getTitle(info.filter))
                .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
                .addLayer(displayPage)
                .set(SlotPosition.top(45), GuiButton.of(FILTER_ITEMS.get(info.filter), (view, clickType) -> {
                    switch (info.filter) {
                        case RECENT -> {
                            GuiManager.openViews.remove(view.getInventoryView());
                            GuiManager.open(view.getInventoryView().getPlayer(), create(new
                                    GuiInfo.Main(info.page, info.favouritesPage, FilterType.INVENTORY)));
                        }
                        case INVENTORY -> {
                            GuiManager.openViews.remove(view.getInventoryView());
                            GuiManager.open(view.getInventoryView().getPlayer(), create(new
                                    GuiInfo.Main(info.page, info.favouritesPage, FilterType.FAVOURITE)));
                        }
                        case FAVOURITE -> {
                            GuiManager.openViews.remove(view.getInventoryView());
                            GuiManager.open(view.getInventoryView().getPlayer(), create(new
                                    GuiInfo.Main(info.page, info.favouritesPage, FilterType.RECENT)));
                        }
                    }
                }))
                .set(SlotPosition.top(50 ), GuiButton.of(
                        UiItems.NEXT.get().getStack(), (view, click) -> {
                            displayPage.next(view);
                            updatePageIndicator(displayPage, displayPageIndicator, info);
                        }
                ))
                .set(SlotPosition.top(49), GuiButton.of(
                        UiItems.SEARCH.get().getStack(), (view, clickType) -> {
                            GuiManager.openViews.remove(view.getInventoryView());
                            GuiManager.open(view.getInventoryView().getPlayer(), SearchMenu.create());
                        })
                )
                .set(SlotPosition.top(48), GuiButton.of(
                        UiItems.PREV.get().getStack(), (view, click) -> {
                            displayPage.prev(view);
                            updatePageIndicator(displayPage, displayPageIndicator, info);
                        }
                ))
                .set(SlotPosition.top(53), GuiButton.of(
                        UiItems.CLOSE.get().getStack(), (view, click) -> {
                            GuiManager.close(view.getInventoryView().getPlayer());
                        }
                ))
                .onClose(MainMenu::loadBackup)
                .onOpen(view -> {
                    setupBackup(view);
                    filterPage.set(new PaginatedElements(getFilterElements.apply(view),
                            BOTTOM_SLOTS, GuiView.Segment.BOTTOM));
                    view.getGui().getLayers().add(filterPage.get());
//                    TaskUtil.delayedTask(1, () -> {
//                        while (displayPage.getPage() < info.page) displayPage.next(view);
//                        updatePageIndicator(displayPage, displayPageIndicator, info);
//                        if (!info.filter.equals(FilterType.FAVOURITE)) return;
//                        while (filterPage.get().getPage() < info.favouritesPage) filterPage.get().next(view);
//                    });
                });

        if (info.filter == FilterType.FAVOURITE) {
            gui.set(SlotPosition.bottom(5), GuiButton.of(
                    UiItems.NEXT.get().getStack(), (view, click) -> {
                        filterPage.get().next(view);
                        info.favouritesPage = filterPage.get().getPage();
                    })
            ).set(SlotPosition.bottom(3), GuiButton.of(
                    UiItems.PREV.get().getStack(), (view, click) -> {
                        filterPage.get().next(view);
                        info.favouritesPage = filterPage.get().getPage();
                    })
            );
        }
        return gui.build();
    }

    private static Component getTitle(FilterType type) {
        return TextUtil.parse("<white><offset><texture></white><width>Main Menu",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", type.equals(FilterType.INVENTORY) ?
                        Pack.MAIN_MENU_INV.toMiniMessageString() : Pack.MAIN_MENU.toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170)));
    }

    private static ItemStack createFilterItem(FilterType type) {
        String[] holder = new String[] {
                type == FilterType.RECENT ? "<!italic><white><bold>Recent</bold><white>" : "<!italic><gray>Recent</gray>",
                type == FilterType.INVENTORY ? "<!italic><white><bold>Inventory</bold><white>" : "<!italic><gray>Inventory</gray>",
                type == FilterType.FAVOURITE ? "<!italic><white><bold>Favourites</bold><white>" : "<!italic><gray>Favourites</gray>"
        };
        Item filterItem = UiItems.FILTER.get().clone();
        Item.Tooltip tl = filterItem.tooltip;
        tl.lines.clear();
        tl.addLine(TextUtil.parse(holder[0]));
        tl.addLine(TextUtil.parse(holder[1]));
        tl.addLine(TextUtil.parse(holder[2]));
        filterItem.updateTooltip();
        return filterItem.getStack();
    }
    static ItemStack createPageIndicator() {
        ItemStack pageIndicator = new ItemStack(Material.BOOK);
        pageIndicator.setData(DataComponentTypes.ITEM_NAME, TextUtil.parse("<green>Page</green>"));
        return pageIndicator;
    }
    static void setupBackup(GuiView view) {
        Player player = (Player) view.getInventoryView().getPlayer();
        INVENTORY_BACKUPS.put(player.getUniqueId(), player.getInventory().getContents());
        ItemStack[] bottomContents = view.getBottom().getContents();
        Arrays.fill(bottomContents, ItemStack.empty());
        view.getBottom().setContents(bottomContents);
    }
    static void loadBackup(GuiView view) {
        view.getTop().setItem(0, ItemStack.of(Material.AIR));
        ItemStack[] bottomContents = view.getBottom().getContents();
        Arrays.fill(bottomContents, ItemStack.empty());
        view.getBottom().setContents(bottomContents);

        Player player = (Player) view.getInventoryView().getPlayer();
        ItemStack[] backup = INVENTORY_BACKUPS.remove(player.getUniqueId());
        if (backup != null) player.getInventory().setContents(backup);
    }
    static void populateElements(List<GuiElement> elements, GuiInfo info) {
        for (int i = 0; i < MainMenu.ITEMS.size(); i++) {
            ItemStack item = MainMenu.ITEMS.get(i);
            elements.add(new GuiButton(item, (view, clickType) -> {
                if (!clickType.isShiftClick() && clickType.isLeftClick()) {
                    openRecipe(view, item, info, RecipeViewer.RecipeType.RECIPE);
                } else if (clickType.isRightClick()) {
                    openRecipe(view, item, info, RecipeViewer.RecipeType.USE);
                } else if (Input.isShiftLeftClick(clickType)) {
                    addFavourite(view.getInventoryView().getPlayer().getUniqueId(), item);
                }
            }));
        }
    }
    public static void updatePageIndicator(PaginatedElements elements, ItemStack pageItem, GuiInfo info) {
        String loreText = String.format("<green>%d <gray>of</gray> %d <gray>pages</gray></green>",
                elements.getPage() + 1, elements.pageCount());

        pageItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                .lines(Collections.singletonList(TextUtil.parse(loreText)))
                .build());

        if (info instanceof GuiInfo.Main m) m.page = elements.getPage();
        if (info instanceof GuiInfo.Search s) s.page = elements.getPage();
    }
    public static void addToRecents(UUID uuid, ItemStack viewed) {
        NeverEnoughRecipes.updatePrefs(p -> {
            Map<String, List<ItemStack>> map = new HashMap<>(p.recents.get());
            List<ItemStack> recents = map.computeIfAbsent(uuid.toString(), k -> new LinkedList<>());
            recents.removeIf(s -> s.isSimilar(viewed));
            recents.addFirst(viewed.clone());
            while (recents.size() > 15) recents.removeLast();
            p.recents.set(map);
        });
    }
    public static void addFavourite(UUID uuid, ItemStack item) {
        NeverEnoughRecipes.updatePrefs(p -> {
            Map<String, List<ItemStack>> map = new HashMap<>(p.favourites.get());
            List<ItemStack> favourites = map.computeIfAbsent(uuid.toString(), k -> new LinkedList<>());
            if (favourites.contains(item)) return;
            favourites.add(item);
            p.favourites.set(map);
        });
    }
    public static void removeFavourite(UUID uuid, ItemStack item) {
        NeverEnoughRecipes.updatePrefs(p -> {
            Map<String, List<ItemStack>> map = new HashMap<>(p.favourites.get());
            List<ItemStack> favourites = map.computeIfAbsent(uuid.toString(), k -> new LinkedList<>());
            favourites.remove(item);
            p.favourites.set(map);
        });
    }
    public static void openRecipe(GuiView view, ItemStack item, GuiInfo info, RecipeViewer.RecipeType type) {
        if (type.equals(RecipeViewer.RecipeType.USE) && RecipeManager.getUsesForItem(item).isEmpty()) return;
        if (type.equals(RecipeViewer.RecipeType.RECIPE) && RecipeManager.getRecipesForResult(item).isEmpty()) return;

        HumanEntity player = view.getInventoryView().getPlayer();
        GuiManager.openViews.remove(view.getInventoryView());
        if (!(info instanceof GuiInfo.Search)) GuiHistory.push(player.getUniqueId(), info);
        addToRecents(player.getUniqueId(), item);
        GuiManager.open(player, RecipeViewer.create(item, 0, type));
    }

    public static List<ItemStack> sortDisplay(List<ItemStack> items) {
        PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();

        Map<String, List<ItemStack>> byNs = new HashMap<>();
        for (ItemStack stack : items)
            byNs.computeIfAbsent(NamespacedFilterManager.getNamespace(stack), k -> new ArrayList<>()).add(stack);

        Comparator<ItemStack> alpha = Comparator.comparing(s -> {
            Component name = Optional.ofNullable(s.getData(DataComponentTypes.CUSTOM_NAME))
                    .orElse(s.getData(DataComponentTypes.ITEM_NAME));
            return plain.serialize(name == null ? Component.text("") : name);
        }, String.CASE_INSENSITIVE_ORDER);

        List<ItemStack> result = new ArrayList<>();
        if (byNs.containsKey("minecraft")) {
            byNs.get("minecraft").sort(alpha);
            result.addAll(byNs.remove("minecraft"));
        }

        byNs.keySet().stream().sorted(String.CASE_INSENSITIVE_ORDER).forEach(ns -> {
            byNs.get(ns).sort(alpha);
            result.addAll(byNs.get(ns));
        });

        return result;
    }

    public enum FilterType {
        RECENT("lore.ner.recent"), INVENTORY("lore.ner.inventory"), FAVOURITE("lore.ner.favourite");

        public final String name;
        FilterType(String name) {
            this.name = name;
        }
    }
}
