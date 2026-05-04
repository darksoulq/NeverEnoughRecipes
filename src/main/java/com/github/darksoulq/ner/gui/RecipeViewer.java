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
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.ner.gui.element.GuiAnimatedButton;
import com.github.darksoulq.ner.model.ControlAction;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.PlayerSettings;
import com.github.darksoulq.ner.user.UserManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.*;

public class RecipeViewer {
    public enum Type { RECIPE, USE }

    private static final int[] PROVIDER_SLOTS = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    public static Gui create(ItemStack target, Type type) {
        return create(target, type, 0, 0);
    }

    public static Gui create(ItemStack target, Type type, int categoryOffset, int recipePage) {
        List<ParsedRecipeView> allRecipes = type == Type.RECIPE ? RecipeManager.getRecipes(target) : RecipeManager.getUses(target);
        if (allRecipes.isEmpty()) return MainMenu.create(null);

        Map<ItemStack, List<ParsedRecipeView>> grouped = new LinkedHashMap<>();
        for (ParsedRecipeView v : allRecipes) {
            grouped.computeIfAbsent(v.provider(), k -> new ArrayList<>()).add(v);
        }

        List<ItemStack> categories = new ArrayList<>(grouped.keySet());
        int safeCategoryOffset = (categoryOffset % categories.size() + categories.size()) % categories.size();
        ItemStack currentCategory = categories.get(safeCategoryOffset);
        List<ParsedRecipeView> activeRecipes = grouped.get(currentCategory);

        int safeRecipePage = (recipePage % activeRecipes.size() + activeRecipes.size()) % activeRecipes.size();
        ParsedRecipeView currentView = activeRecipes.get(safeRecipePage);

        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><width><title>",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(currentView.offset())),
                Placeholder.parsed("texture", currentView.texture().toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-168)),
                Placeholder.parsed("title", type == Type.RECIPE ? "Recipes" : "Uses")))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .set(SlotPosition.top(48), new GuiButton(UiItems.PREV.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(ctx.view().getPlayer(), create(target, type, safeCategoryOffset, safeRecipePage - 1));
            }))
            .set(SlotPosition.top(50), new GuiButton(UiItems.NEXT.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(ctx.view().getPlayer(), create(target, type, safeCategoryOffset, safeRecipePage + 1));
            }))
            .set(SlotPosition.top(53), new GuiButton(UiItems.CLOSE.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(ctx.view().getPlayer(), MainMenu.create(ctx.view().getPlayer()));
            }));

        ItemStack pageIndicator = new ItemStack(Material.BOOK);
        pageIndicator.setData(DataComponentTypes.ITEM_NAME, TextUtil.parse("<green>Page</green>"));
        pageIndicator.setData(DataComponentTypes.LORE, ItemLore.lore().lines(List.of(
            TextUtil.parse("<green>" + (safeRecipePage + 1) + " <gray>of</gray> " + activeRecipes.size() + "</green>")
        )).build());
        builder.set(SlotPosition.top(49), new GuiItem(pageIndicator));

        PagedLayer<ItemStack> providersLayer = PagedLayer.of(categories, PROVIDER_SLOTS, GuiView.Segment.BOTTOM,
            (cat, index) -> {
                ItemStack display = cat.clone();
                if (index == safeCategoryOffset) {
                    display.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                }
                return new GuiButton(display, ctx -> {
                    InventoryBackupManager.transition(ctx.view());
                    GuiManager.open(ctx.view().getPlayer(), create(target, type, index, 0));
                });
            }
        );
        builder.addLayer(providersLayer);

        builder.set(SlotPosition.bottom(0), new GuiButton(UiItems.PREV.getStack(), ctx -> {
            providersLayer.previous(ctx.view());
            providersLayer.renderTo(ctx.view());
        }));
        builder.set(SlotPosition.bottom(8), new GuiButton(UiItems.NEXT.getStack(), ctx -> {
            providersLayer.next(ctx.view());
            providersLayer.renderTo(ctx.view());
        }));

        for (Map.Entry<Integer, List<ItemStack>> entry : currentView.slots().entrySet()) {
            List<ItemStack> cycled = entry.getValue();
            if (cycled.isEmpty()) continue;

            if (cycled.size() == 1) {
                builder.set(SlotPosition.top(entry.getKey()), new GuiButton(cycled.getFirst(), ctx ->
                    handleItemClick(ctx.view().getPlayer(), ctx.view(), cycled.getFirst(), ctx.clickType())
                ));
            } else {
                builder.set(SlotPosition.top(entry.getKey()), new GuiAnimatedButton(cycled, 20, ctx ->
                    handleItemClick(ctx.view().getPlayer(), ctx.view(), ctx.currentItem(), ctx.clickType())
                ));
            }
        }

        return builder.onOpen(view -> {
            InventoryBackupManager.setup(view);
            providersLayer.renderTo(view);
        }).onClose(InventoryBackupManager::restore).build();
    }

    private static void handleItemClick(Player player, GuiView view, ItemStack item, ClickType click) {
        if (item == null || item.isEmpty()) return;
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        ControlAction action = settings.resolveAction(click);
        if (action == null) return;

        if (action == ControlAction.VIEW_RECIPE && !RecipeManager.getRecipes(item).isEmpty()) {
            InventoryBackupManager.transition(view);
            GuiManager.open(player, create(item, Type.RECIPE, 0, 0));
        } else if (action == ControlAction.VIEW_USES && !RecipeManager.getUses(item).isEmpty()) {
            InventoryBackupManager.transition(view);
            GuiManager.open(player, create(item, Type.USE, 0, 0));
        }
    }
}