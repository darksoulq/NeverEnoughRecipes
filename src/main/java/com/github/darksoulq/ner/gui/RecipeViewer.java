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
import com.github.darksoulq.ner.model.PagedSection;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.PlayerSettings;
import com.github.darksoulq.ner.user.UserManager;
import com.github.darksoulq.ner.util.ProbabilityParser;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
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
        return create(target, type, 0, 0, 0);
    }

    public static Gui create(ItemStack target, Type type, int categoryOffset, int recipePage) {
        return create(target, type, categoryOffset, recipePage, 0);
    }

    public static Gui create(ItemStack target, Type type, int categoryOffset, int recipePage, int subPage) {
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
                GuiManager.open(ctx.view().getPlayer(), create(target, type, safeCategoryOffset, safeRecipePage - 1, 0));
            }))
            .set(SlotPosition.top(50), new GuiButton(UiItems.NEXT.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(ctx.view().getPlayer(), create(target, type, safeCategoryOffset, safeRecipePage + 1, 0));
            }))
            .set(SlotPosition.top(53), new GuiButton(UiItems.CLOSE.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                GuiManager.open(ctx.view().getPlayer(), MainMenu.create(ctx.view().getPlayer()));
            }));

        for (Map.Entry<Integer, List<ItemStack>> entry : currentView.slots().entrySet()) {
            int slot = entry.getKey();
            List<ItemStack> cycled = entry.getValue();
            if (cycled.isEmpty()) continue;

            List<ItemStack> displayCycled = new ArrayList<>();

            for (ItemStack st : cycled) {
                ItemStack display = IngredientManager.applyModifiers(st.clone());
                String probExpr = currentView.probabilities().get(st);
                if (probExpr != null) applyProbabilityLore(display, probExpr);
                displayCycled.add(display);
            }

            if (cycled.size() == 1) {
                builder.set(SlotPosition.top(slot), new GuiButton(displayCycled.getFirst(), ctx ->
                    handleItemClick(ctx.view().getPlayer(), ctx.view(), cycled.getFirst(), ctx.clickType())
                ));
            } else {
                builder.set(SlotPosition.top(slot), new GuiAnimatedButton(displayCycled, 20, ctx -> {
                    ItemStack clickedDisplay = ctx.currentItem();
                    int idx = displayCycled.indexOf(clickedDisplay);
                    ItemStack original = idx != -1 ? cycled.get(idx) : cycled.getFirst();
                    handleItemClick(ctx.view().getPlayer(), ctx.view(), original, ctx.clickType());
                }));
            }
        }

        int maxSectionPages = 1;

        for (PagedSection section : currentView.pagedSections()) {
            int[] targetSlots = section.slots();
            List<ItemStack> items = section.items();
            if (targetSlots == null || targetSlots.length == 0 || items == null || items.isEmpty()) continue;

            int slotsPerPage = targetSlots.length;
            int pages = (int) Math.ceil((double) items.size() / slotsPerPage);
            if (pages > maxSectionPages) maxSectionPages = pages;

            int currentSectionPage = (subPage % pages + pages) % pages;
            int start = currentSectionPage * slotsPerPage;

            for (int i = 0; i < slotsPerPage && (start + i) < items.size(); i++) {
                ItemStack originalItem = items.get(start + i);
                if (originalItem != null && !originalItem.isEmpty()) {
                    int slot = targetSlots[i];
                    ItemStack displayItem = IngredientManager.applyModifiers(originalItem.clone());
                    String probExpr = currentView.probabilities().get(originalItem);
                    if (probExpr != null) applyProbabilityLore(displayItem, probExpr);

                    builder.set(SlotPosition.top(slot), new GuiButton(displayItem, ctx ->
                        handleItemClick(ctx.view().getPlayer(), ctx.view(), originalItem, ctx.clickType())
                    ));
                }
            }

            if (pages > 1) {
                if (section.prevButton() != null && section.prevButton().slot() != -1) {
                    ItemStack icon = section.prevButton().item() != null ? section.prevButton().item() : UiItems.PREV.getStack();
                    builder.set(SlotPosition.top(section.prevButton().slot()), new GuiButton(icon, ctx -> {
                        InventoryBackupManager.transition(ctx.view());
                        GuiManager.open(ctx.view().getPlayer(), create(target, type, safeCategoryOffset, safeRecipePage, subPage - 1));
                    }));
                }
                if (section.nextButton() != null && section.nextButton().slot() != -1) {
                    ItemStack icon = section.nextButton().item() != null ? section.nextButton().item() : UiItems.NEXT.getStack();
                    builder.set(SlotPosition.top(section.nextButton().slot()), new GuiButton(icon, ctx -> {
                        InventoryBackupManager.transition(ctx.view());
                        GuiManager.open(ctx.view().getPlayer(), create(target, type, safeCategoryOffset, safeRecipePage, subPage + 1));
                    }));
                }
            }
        }

        ItemStack pageIndicator = new ItemStack(Material.BOOK);
        pageIndicator.setData(DataComponentTypes.ITEM_NAME, TextUtil.parse("<green>Page</green>"));
        List<Component> loreLines = new ArrayList<>();
        loreLines.add(TextUtil.parse("<green>" + (safeRecipePage + 1) + " <gray>of</gray> " + activeRecipes.size() + "</green>"));

        if (maxSectionPages > 1) {
            int displaySubPage = (subPage % maxSectionPages + maxSectionPages) % maxSectionPages;
            loreLines.add(TextUtil.parse("<yellow>Section " + (displaySubPage + 1) + " <gray>of</gray> " + maxSectionPages + "</yellow>"));
        }

        pageIndicator.setData(DataComponentTypes.LORE, ItemLore.lore().lines(loreLines).build());
        builder.set(SlotPosition.top(49), new GuiItem(pageIndicator));

        PagedLayer<ItemStack> providersLayer = PagedLayer.of(categories, PROVIDER_SLOTS, GuiView.Segment.BOTTOM,
            (cat, index) -> {
                ItemStack display = cat.clone();
                if (index == safeCategoryOffset) {
                    display.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                }
                return new GuiButton(display, ctx -> {
                    InventoryBackupManager.transition(ctx.view());
                    GuiManager.open(ctx.view().getPlayer(), create(target, type, index, 0, 0));
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

        return builder.onOpen(view -> {
            InventoryBackupManager.setup(view);
            providersLayer.renderTo(view);
        }).onClose(InventoryBackupManager::restore).build();
    }

    private static void applyProbabilityLore(ItemStack item, String expression) {
        if (item == null || item.isEmpty() || expression == null) return;
        Component probComp = ProbabilityParser.parseProbability(expression);
        if (probComp == null) return;

        List<Component> lines = new ArrayList<>();
        if (item.hasData(DataComponentTypes.LORE)) {
            lines.addAll(item.getData(DataComponentTypes.LORE).lines());
        }
        lines.add(Component.empty());
        lines.add(probComp);
        item.setData(DataComponentTypes.LORE, ItemLore.lore().lines(lines).build());
    }

    private static void handleItemClick(Player player, GuiView view, ItemStack item, ClickType click) {
        if (item == null || item.isEmpty()) return;
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        ControlAction action = settings.resolveAction(click);
        if (action == null) return;

        if (action == ControlAction.VIEW_RECIPE && !RecipeManager.getRecipes(item).isEmpty()) {
            InventoryBackupManager.transition(view);
            GuiManager.open(player, create(item, Type.RECIPE, 0, 0, 0));
        } else if (action == ControlAction.VIEW_USES && !RecipeManager.getUses(item).isEmpty()) {
            InventoryBackupManager.transition(view);
            GuiManager.open(player, create(item, Type.USE, 0, 0, 0));
        }
    }
}