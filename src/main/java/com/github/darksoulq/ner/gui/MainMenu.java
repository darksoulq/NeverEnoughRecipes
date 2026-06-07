package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.element.StateCycleElement;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.ner.model.ControlAction;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.PlayerSettings;
import com.github.darksoulq.ner.user.UserManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MainMenu {
    public enum FilterMode {
        RECENT("<!italic><gray>Mode: <white>Recent"),
        INVENTORY("<!italic><gray>Mode: <white>Inventory"),
        FAVOURITE("<!italic><gray>Mode: <white>Favourites");

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
        27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    private static final int[] BOTTOM_SLOTS_INV = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };

    public static Gui create(Player player) {
        return create(player, FilterMode.RECENT, 0, 0);
    }

    public static Gui create(Player player, FilterMode mode, int topPage, int bottomPage) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());

        PagedLayer<ItemStack> topLayer = PagedLayer.of(IngredientManager.getItems(), TOP_SLOTS, GuiView.Segment.TOP,
            (item, index) -> {
                ItemStack display = IngredientManager.applyModifiers(player, item.clone());
                return new GuiButton(display, ctx -> handleItemClick(ctx.view(), item, ctx.clickType(), mode, topPage, bottomPage));
            }
        );

        List<ItemStack> bottomSource = new ArrayList<>();
        int[] bottomSlots = BOTTOM_SLOTS;

        if (mode == FilterMode.INVENTORY) {
            bottomSlots = BOTTOM_SLOTS_INV;
            ItemStack[] backup = InventoryBackupManager.getBackup(player.getUniqueId());
            if (backup != null) {
                for (int i = 9; i < 36; i++) bottomSource.add(backup[i] != null ? backup[i] : ItemStack.empty());
                for (int i = 0; i < 9; i++) bottomSource.add(backup[i] != null ? backup[i] : ItemStack.empty());
            }
        } else if (mode == FilterMode.FAVOURITE) {
            bottomSource.addAll(settings.favourites.get());
        } else {
            List<ItemStack> recents = settings.recents.get();
            bottomSource.addAll(recents.subList(0, Math.min(10, recents.size())));
        }

        PagedLayer<ItemStack> bottomLayer = PagedLayer.of(bottomSource, bottomSlots, GuiView.Segment.BOTTOM,
            (item, index) -> {
                if (item == null || item.isEmpty()) return new GuiItem(ItemStack.empty());
                ItemStack display = item.clone();
                if (mode != FilterMode.INVENTORY) {
                    display = IngredientManager.applyModifiers(player, display);
                }
                return new GuiButton(display, ctx -> handleItemClick(ctx.view(), item, ctx.clickType(), mode, topPage, bottomPage));
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
            GuiManager.open(player, create(player, newMode, topLayer.getPage(), 0));
        });

        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><width>Main Menu",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", mode == FilterMode.INVENTORY ? Pack.MAIN_MENU_INV.toMiniMessageString() : Pack.MAIN_MENU.toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .addLayer(topLayer)
            .addLayer(bottomLayer)
            .set(SlotPosition.top(45), cycleElement)
            .set(SlotPosition.top(48), new GuiButton(UiItems.PREV.getStack(), ctx -> { topLayer.previous(ctx.view()); topLayer.renderTo(ctx.view()); }))
            .set(SlotPosition.top(49), new GuiButton(UiItems.SEARCH.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(player, SearchMenu.create(player));
            }))
            .set(SlotPosition.top(50), new GuiButton(UiItems.NEXT.getStack(), ctx -> { topLayer.next(ctx.view()); topLayer.renderTo(ctx.view()); }))
            .set(SlotPosition.top(53), new GuiButton(UiItems.CLOSE.getStack(), ctx -> GuiManager.close(player)));

        if (mode == FilterMode.FAVOURITE) {
            builder.set(SlotPosition.bottom(0), new GuiButton(UiItems.PREV.getStack(), ctx -> { bottomLayer.previous(ctx.view()); bottomLayer.renderTo(ctx.view()); }));
            builder.set(SlotPosition.bottom(8), new GuiButton(UiItems.NEXT.getStack(), ctx -> { bottomLayer.next(ctx.view()); bottomLayer.renderTo(ctx.view()); }));
        }

        return builder.onOpen(view -> {
            InventoryBackupManager.setup(view);
            while (topLayer.getPage() < topPage) topLayer.next(view);
            while (bottomLayer.getPage() < bottomPage) bottomLayer.next(view);
            topLayer.renderTo(view);
            bottomLayer.renderTo(view);
        }).onClose(InventoryBackupManager::restore).build();
    }

    private static void handleItemClick(GuiView view, ItemStack item, ClickType click, FilterMode mode, int tp, int bp) {
        if (item == null || item.isEmpty()) return;
        Player player = view.getPlayer();
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        ControlAction action = settings.resolveAction(click);
        if (action == null) return;

        if (action == ControlAction.VIEW_RECIPE && !RecipeManager.getRecipes(item).isEmpty()) {
            InventoryBackupManager.transition(view);
            addToRecents(player, item);
            GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.RECIPE));
        } else if (action == ControlAction.VIEW_USES && !RecipeManager.getUses(item).isEmpty()) {
            InventoryBackupManager.transition(view);
            addToRecents(player, item);
            GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.USE));
        } else if (action == ControlAction.TOGGLE_BOOKMARK) {
            toggleBookmark(player, item);
            InventoryBackupManager.transition(view);
            GuiManager.open(player, create(player, mode, tp, bp));
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