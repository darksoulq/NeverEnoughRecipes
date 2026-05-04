package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.abyssallib.world.item.Items;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SearchMenu {
    private static final int[] SLOTS = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    public static Gui create(Player player) {
        List<ItemStack> allItems = IngredientManager.getItems();
        final String[] activeInput = { "" };

        PagedLayer<ItemStack> page = PagedLayer.of(allItems, SLOTS, com.github.darksoulq.abyssallib.world.gui.GuiView.Segment.BOTTOM,
            (item, index) -> new GuiButton(item, ctx -> {
                PlayerSettings settings = UserManager.get(player.getUniqueId());
                ControlAction action = settings.resolveAction(ctx.clickType());
                if (action == null) return;

                if (action == ControlAction.VIEW_RECIPE && !RecipeManager.getRecipes(item).isEmpty()) {
                    InventoryBackupManager.transition(ctx.view());
                    GuiManager.open(player, RecipeViewer.create(item, RecipeViewer.Type.RECIPE));
                } else if (action == ControlAction.VIEW_USES && !RecipeManager.getUses(item).isEmpty()) {
                    InventoryBackupManager.transition(ctx.view());
                    GuiManager.open(player, RecipeViewer.create(item, RecipeViewer.Type.USE));
                }
            })
        );

        ItemStack invisibleFiller = Items.INVISIBLE_ITEM.getStack();
        invisibleFiller.setData(DataComponentTypes.ITEM_NAME, Component.text(""));
        invisibleFiller.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());

        return Gui.builder(MenuType.ANVIL, TextUtil.parse("<white><offset><texture></white><width>Search",
                Placeholder.parsed("texture", Pack.SEARCH_MENU.toMiniMessageString()),
                Placeholder.parsed("offset", com.github.darksoulq.abyssallib.server.resource.util.TextOffset.getOffsetMinimessage(-60)),
                Placeholder.parsed("width", com.github.darksoulq.abyssallib.server.resource.util.TextOffset.getOffsetMinimessage(-170))))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .addLayer(page)
            .set(SlotPosition.bottom(0), new GuiButton(UiItems.PREV.getStack(), ctx -> { page.previous(ctx.view()); page.renderTo(ctx.view()); }))
            .set(SlotPosition.bottom(8), new GuiButton(UiItems.NEXT.getStack(), ctx -> { page.next(ctx.view()); page.renderTo(ctx.view()); }))
            .set(SlotPosition.top(0), new GuiItem(invisibleFiller))
            .onTick(view -> {
                if (!(view.getInventoryView() instanceof AnvilView anvilView)) return;
                String nextInput = Optional.ofNullable(anvilView.getRenameText()).orElse("");
                if (activeInput[0].equals(nextInput)) return;

                activeInput[0] = nextInput;
                Set<ItemStack> searchResults = new HashSet<>(IngredientManager.search(nextInput));
                page.setFilter(searchResults::contains);
                page.renderTo(view);
            })
            .onOpen(view -> {
                InventoryBackupManager.setup(view);
                page.renderTo(view);
            })
            .onClose(InventoryBackupManager::restore)
            .build();
    }
}