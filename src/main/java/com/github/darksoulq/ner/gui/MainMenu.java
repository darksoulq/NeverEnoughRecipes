package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.GuiView.Segment;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.element.StateCycleElement;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.ner.gui.element.GuiAnimatedButton;
import com.github.darksoulq.ner.gui.layer.DynamicPagedLayer;
import com.github.darksoulq.ner.model.ControlAction;
import com.github.darksoulq.ner.model.GuiEntry;
import com.github.darksoulq.ner.model.ItemGroup;
import com.github.darksoulq.ner.registry.GroupManager;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.PlayerSettings;
import com.github.darksoulq.ner.user.UserManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class MainMenu {
    public enum FilterMode {
        ALL("<!italic><gray>Mode: <white>All Items"),
        RECENT("<!italic><gray>Mode: <white>Recent"),
        FAVOURITE("<!italic><gray>Mode: <white>Favourites"),
        CRAFTABLE("<!italic><gray>Mode: <white>Craftable");

        public final String title;
        FilterMode(String title) { this.title = title; }
    }

    private static final int[] TOP_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final int[] BOTTOM_SLOTS = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };

    private static void updateArrows(GuiView view, DynamicPagedLayer<?> topLayer) {
        Inventory top = view.getTop();
        top.setItem(48, topLayer.getPage() > 0 ? UiItems.PREV.getStack() : null);
        top.setItem(50, topLayer.getPage() < topLayer.getPageCount() - 1 ? UiItems.NEXT.getStack() : null);
    }

    public static Gui create(Player player) {
        GuiHistory.clear(player);
        return create(player, FilterMode.ALL, 0, new HashSet<>());
    }

    public static Gui create(Player player, FilterMode mode, int topPage, Set<String> expanded) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());

        List<GuiEntry> allEntries = GroupManager.buildEntries(expanded);
        List<GuiEntry> topSource = new ArrayList<>();

        if (mode == FilterMode.RECENT) {
            List<ItemStack> recents = settings.recents.get();
            for (ItemStack item : recents.subList(0, Math.min(10, recents.size()))) {
                topSource.add(new GuiEntry.ItemEntry(item));
            }
        } else if (mode == FilterMode.FAVOURITE) {
            for (ItemStack item : settings.favourites.get()) {
                topSource.add(new GuiEntry.ItemEntry(item));
            }
        } else if (mode == FilterMode.CRAFTABLE) {
            for (GuiEntry entry : allEntries) {
                if (entry instanceof GuiEntry.ItemEntry ie) {
                    if (RecipeManager.isCraftable(player, ie.item())) {
                        topSource.add(entry);
                    }
                } else if (entry instanceof GuiEntry.GroupEntry ge) {
                    boolean craftable = false;
                    for (ItemStack gi : ge.group().items()) {
                        if (RecipeManager.isCraftable(player, gi)) {
                            craftable = true;
                            break;
                        }
                    }
                    if (craftable) topSource.add(entry);
                } else if (entry instanceof GuiEntry.GroupCollapseEntry) {
                    topSource.add(entry);
                }
            }
        } else {
            topSource = allEntries;
        }

        DynamicPagedLayer<GuiEntry> topLayer = new DynamicPagedLayer<>(topSource, TOP_SLOTS, Segment.TOP,
            (entry, index) -> {
                if (entry instanceof GuiEntry.ItemEntry ie) {
                    ItemStack display = IngredientManager.applyModifiers(player, ie.item().clone());
                    return new GuiButton(display, ctx -> handleItemClick(ctx.view(), ie.item(), ctx.clickType(), mode, expanded));
                } else if (entry instanceof GuiEntry.GroupEntry ge) {
                    ItemGroup group = ge.group();
                    if (group.items().isEmpty()) return new GuiItem(ItemStack.empty());

                    if (group.animate() && group.items().size() > 1) {
                        List<ItemStack> frames = new ArrayList<>();
                        for (ItemStack gi : group.items()) {
                            ItemStack frame = IngredientManager.applyModifiers(player, gi.clone());
                            if (!group.title().equals(Component.empty())) {
                                frame.setData(DataComponentTypes.ITEM_NAME, group.title());
                            }
                            frames.add(frame);
                        }
                        return new GuiAnimatedButton(frames, 20, ctx -> {
                            expanded.add(group.id());
                            DynamicPagedLayer<GuiEntry> layer = (DynamicPagedLayer<GuiEntry>) ctx.view().getGui().getLayers().getFirst();
                            layer.updateSource(GroupManager.buildEntries(expanded), ctx.view());
                            updateArrows(ctx.view(), layer);
                        });
                    } else {
                        ItemStack display = IngredientManager.applyModifiers(player, group.items().getFirst().clone());
                        if (!group.title().equals(Component.empty())) {
                            display.setData(DataComponentTypes.ITEM_NAME, group.title());
                        }
                        return new GuiButton(display, ctx -> {
                            expanded.add(group.id());
                            DynamicPagedLayer<GuiEntry> layer = (DynamicPagedLayer<GuiEntry>) ctx.view().getGui().getLayers().getFirst();
                            layer.updateSource(GroupManager.buildEntries(expanded), ctx.view());
                            updateArrows(ctx.view(), layer);
                        });
                    }
                } else if (entry instanceof GuiEntry.GroupCollapseEntry gce) {
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    if (!gce.group().title().equals(Component.empty())) {
                        barrier.setData(DataComponentTypes.ITEM_NAME, gce.group().title());
                    }
                    return new GuiButton(barrier, ctx -> {
                        expanded.remove(gce.group().id());
                        DynamicPagedLayer<GuiEntry> layer = (DynamicPagedLayer<GuiEntry>) ctx.view().getGui().getLayers().getFirst();
                        layer.updateSource(GroupManager.buildEntries(expanded), ctx.view());
                        updateArrows(ctx.view(), layer);
                    });
                }
                return new GuiItem(ItemStack.empty());
            }
        );

        List<ItemStack> bottomSource = new ArrayList<>();
        ItemStack[] backup = InventoryBackupManager.getBackup(player.getUniqueId());

        if (backup != null) {
            for (int i = 9; i < 36; i++) bottomSource.add(backup[i] != null ? backup[i] : ItemStack.empty());
            for (int i = 0; i < 9; i++) bottomSource.add(backup[i] != null ? backup[i] : ItemStack.empty());
        } else {
            ItemStack[] inv = player.getInventory().getContents();
            for (int i = 9; i < 36; i++) bottomSource.add(inv[i] != null ? inv[i] : ItemStack.empty());
            for (int i = 0; i < 9; i++) bottomSource.add(inv[i] != null ? inv[i] : ItemStack.empty());
        }

        PagedLayer<ItemStack> bottomLayer = PagedLayer.of(bottomSource, BOTTOM_SLOTS, Segment.BOTTOM,
            (item, idx) -> {
                if (item == null || item.isEmpty()) return new GuiItem(ItemStack.empty());
                ItemStack display = item.clone();
                return new GuiButton(display, ctx -> handleItemClick(ctx.view(), item, ctx.clickType(), mode, expanded));
            }
        );

        List<StateCycleElement.State<FilterMode>> states = new ArrayList<>();
        for (FilterMode m : FilterMode.values()) {
            ItemStack icon = UiItems.FILTER.getStack().clone();
            icon.editMeta(meta -> meta.displayName(TextUtil.parse(m.title)));
            states.add(new StateCycleElement.State<>(icon, m));
        }

        StateCycleElement<FilterMode> cycleElement = StateCycleElement.of(states, mode.ordinal(), newMode -> {
            InventoryBackupManager.transition(GuiManager.OPEN_VIEWS.get(player.getOpenInventory()));
            GuiManager.open(player, create(player, newMode, topLayer.getPage(), expanded));
        });

        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><width>Main Menu",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", Pack.MAIN_MENU_INV.toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .addLayer(topLayer)
            .addLayer(bottomLayer)
            .set(SlotPosition.top(45), cycleElement)
            .set(SlotPosition.top(48), new GuiButton(UiItems.PREV.getStack(), ctx -> {
                if (topLayer.getPage() > 0) {
                    topLayer.previous(ctx.view());
                    updateArrows(ctx.view(), topLayer);
                }
            }) {
                @Override
                public ItemStack render(GuiView view, int slot) {
                    return topLayer.getPage() > 0 ? super.render(view, slot) : null;
                }
            })
            .set(SlotPosition.top(49), new GuiButton(UiItems.SEARCH.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(player, SearchMenu.create(player));
            }))
            .set(SlotPosition.top(50), new GuiButton(UiItems.NEXT.getStack(), ctx -> {
                if (topLayer.getPage() < topLayer.getPageCount() - 1) {
                    topLayer.next(ctx.view());
                    updateArrows(ctx.view(), topLayer);
                }
            }) {
                @Override
                public ItemStack render(GuiView view, int slot) {
                    return topLayer.getPage() < topLayer.getPageCount() - 1 ? super.render(view, slot) : null;
                }
            })
            .set(SlotPosition.top(53), new GuiButton(UiItems.CLOSE.getStack(), ctx -> GuiManager.close(player)));

        return builder.onOpen(view -> {
            InventoryBackupManager.setup(view);
            int maxTopPage = Math.max(0, topLayer.getPageCount() - 1);
            int safeTopPage = Math.max(0, Math.min(topPage, maxTopPage));
            while (topLayer.getPage() < safeTopPage) topLayer.next(view);
            topLayer.renderTo(view);
            bottomLayer.renderTo(view);
            updateArrows(view, topLayer);
        }).onClose(InventoryBackupManager::restore).build();
    }

    private static void handleItemClick(GuiView view, ItemStack item, ClickType click, FilterMode mode, Set<String> expanded) {
        if (item == null || item.isEmpty()) return;
        Player player = view.getPlayer();
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        ControlAction action = settings.resolveAction(click);
        if (action == null) return;

        DynamicPagedLayer<?> topLayer = (DynamicPagedLayer<?>) view.getGui().getLayers().getFirst();
        int currentPage = topLayer.getPage();

        if (action == ControlAction.VIEW_RECIPE && !RecipeManager.getRecipes(item).isEmpty()) {
            GuiHistory.push(player, () -> GuiManager.open(player, create(player, mode, currentPage, expanded)));
            InventoryBackupManager.transition(view);
            addToRecents(player, item);
            GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.RECIPE));
        } else if (action == ControlAction.VIEW_USES && !RecipeManager.getUses(item).isEmpty()) {
            GuiHistory.push(player, () -> GuiManager.open(player, create(player, mode, currentPage, expanded)));
            InventoryBackupManager.transition(view);
            addToRecents(player, item);
            GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.USE));
        } else if (action == ControlAction.TOGGLE_BOOKMARK) {
            toggleBookmark(player, item);
            InventoryBackupManager.transition(view);
            GuiManager.open(player, create(player, mode, currentPage, expanded));
        }
    }

    private static void addToRecents(Player player, ItemStack viewed) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        ItemStack one = viewed.asOne();
        List<ItemStack> recents = new ArrayList<>(settings.recents.get());
        recents.removeIf(s -> s.isSimilar(one));
        recents.addFirst(one.clone());
        if (recents.size() > 15) recents = recents.subList(0, 15);
        settings.recents.set(recents);
        settings.config.save();
    }

    private static void toggleBookmark(Player player, ItemStack item) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        List<ItemStack> favs = new ArrayList<>(settings.favourites.get());
        ItemStack normalized = item.asOne();
        if (favs.contains(normalized)) favs.remove(normalized);
        else favs.add(normalized);
        settings.favourites.set(favs);
        settings.config.save();
    }
}