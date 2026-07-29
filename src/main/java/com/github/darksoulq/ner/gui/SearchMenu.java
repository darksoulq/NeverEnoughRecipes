package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.GuiView.Segment;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.ner.gui.layer.DynamicPagedLayer;
import com.github.darksoulq.ner.model.ControlAction;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.PlayerSettings;
import com.github.darksoulq.ner.user.UserManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class SearchMenu {
    private static final int[] SLOTS = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    private static void updateArrows(GuiView view, DynamicPagedLayer<?> page) {
        Inventory bottom = view.getBottom();
        bottom.setItem(0, page.getPage() > 0 ? UiItems.PREV.getStack() : null);
        bottom.setItem(8, page.getPage() < page.getPageCount() - 1 ? UiItems.NEXT.getStack() : null);
    }

    public static Gui create(Player player) {
        List<ItemStack> allItems = IngredientManager.getItems();
        final String[] activeInput = { "" };
        final boolean[] transitioning = { false };

        DynamicPagedLayer<ItemStack> page = new DynamicPagedLayer<>(allItems, SLOTS, Segment.BOTTOM,
            (item, ignored) -> {
                ItemStack display = IngredientManager.applyModifiers(player, item.clone());
                return new GuiButton(display, ctx -> {
                    PlayerSettings settings = UserManager.get(player.getUniqueId());
                    ControlAction action = settings.resolveAction(ctx.clickType());
                    if (action == null) return;

                    if (action == ControlAction.VIEW_RECIPE && !RecipeManager.getRecipes(item).isEmpty()) {
                        transitioning[0] = true;
                        GuiHistory.push(player, () -> GuiManager.open(player, create(player)));
                        InventoryBackupManager.transition(ctx.view());
                        GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.RECIPE));
                    } else if (action == ControlAction.VIEW_USES && !RecipeManager.getUses(item).isEmpty()) {
                        transitioning[0] = true;
                        GuiHistory.push(player, () -> GuiManager.open(player, create(player)));
                        InventoryBackupManager.transition(ctx.view());
                        GuiManager.open(player, RecipeViewer.create(player, item, RecipeViewer.Type.USE));
                    }
                });
            }
        );

        ItemStack invisibleFiller = Items.INVISIBLE_ITEM.getStack();
        invisibleFiller.setData(DataComponentTypes.ITEM_NAME, Component.text(""));
        invisibleFiller.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());

        return Gui.builder(MenuType.ANVIL, TextUtil.parse("<white><offset><texture></white><width>Search",
                Placeholder.parsed("texture", Pack.SEARCH_MENU.toMiniMessageString()),
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-60)),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .addLayer(page)
            .set(SlotPosition.bottom(0), new GuiButton(UiItems.PREV.getStack(), ctx -> {
                if (page.getPage() > 0) {
                    page.previous(ctx.view());
                    updateArrows(ctx.view(), page);
                }
            }) {
                @Override
                public ItemStack render(GuiView view, int slot) {
                    return page.getPage() > 0 ? super.render(view, slot) : null;
                }
            })
            .set(SlotPosition.bottom(8), new GuiButton(UiItems.NEXT.getStack(), ctx -> {
                if (page.getPage() < page.getPageCount() - 1) {
                    page.next(ctx.view());
                    updateArrows(ctx.view(), page);
                }
            }) {
                @Override
                public ItemStack render(GuiView view, int slot) {
                    return page.getPage() < page.getPageCount() - 1 ? super.render(view, slot) : null;
                }
            })
            .set(SlotPosition.top(0), new GuiItem(invisibleFiller))
            .onTick(view -> {
                if (!(view.getInventoryView() instanceof AnvilView anvilView)) return;
                String nextInput = Optional.ofNullable(anvilView.getRenameText()).orElse("");

                if (!activeInput[0].equals(nextInput)) {
                    activeInput[0] = nextInput;
                    List<ItemStack> searchResults = nextInput.isEmpty() ? allItems : IngredientManager.search(nextInput);
                    page.updateSource(searchResults, view);
                    updateArrows(view, page);
                }
            })
            .onOpen(view -> {
                InventoryBackupManager.setup(view);
                page.renderTo(view);
                updateArrows(view, page);
            })
            .onClose(view -> {
                InventoryBackupManager.restore(view);
                if (!transitioning[0] && UserManager.get(player.getUniqueId()).returnToMenuOnSearchClose.get()) {
                    AbyssalLib.SCHEDULER.schedule(() -> GuiManager.open(player, MainMenu.create(player))).after(1L, Clock.TICKS).entity(player).once();
                }
            }).build();
    }
}