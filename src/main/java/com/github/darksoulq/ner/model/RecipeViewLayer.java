package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.ner.data.Input;
import com.github.darksoulq.ner.gui.GuiInfo;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.gui.RecipeViewer;
import com.github.darksoulq.ner.layout.PaginatedSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeViewLayer implements GuiLayer {
    public final ParsedRecipeView view;
    private final Map<SlotPosition, GuiElement> items = new HashMap<>();
    private final List<PagedLayer<GuiElement>> pages = new ArrayList<>();
    private int lastTime = 0;

    public RecipeViewLayer(ParsedRecipeView view, GuiInfo info) {
        this.view = view;

        for (Map.Entry<Integer, List<ItemStack>> entry : view.getSlotMap().entrySet()) {
            int slot = entry.getKey();
            List<ItemStack> stackList = entry.getValue();
            SlotPosition position = SlotPosition.top(slot);

            GuiElement animated = createButton(stackList, info, true);

            items.put(position, animated);
        }

        if (view.getSections() == null) return;
        for (PaginatedSection section : view.getSections()) {
            List<GuiElement> elements = new ArrayList<>();
            section.getStacks().forEach(v -> elements.add(createButton(List.of(v), info, false)));

            PagedLayer<GuiElement> page = PagedLayer.of(elements, section.getSlots(), GuiView.Segment.TOP);
            pages.add(page);

            items.put(SlotPosition.top(section.getNextButton().slot()), GuiButton.of(section.getNextButton().display(),
                (ctx) -> {
                    page.next(ctx.view());
                    page.renderTo(ctx.view());
                }));
            items.put(SlotPosition.top(section.getPrevButton().slot()), GuiButton.of(section.getPrevButton().display(),
                (ctx) -> {
                    page.previous(ctx.view());
                    page.renderTo(ctx.view());
                }));
        }
    }

    @Override
    public void cleanup(GuiView view) {
        items.keySet().forEach(sp -> {
            view.getGui().getElements().remove(sp);
            if (sp.segment() == GuiView.Segment.TOP) {
                view.getTop().setItem(sp.index(), null);
            } else {
                view.getBottom().setItem(sp.index(), null);
            }
        });
        for (PagedLayer<GuiElement> page : pages) {
            page.cleanup(view);
            page.setFilter(t -> true);
        }
    }

    @Override
    public void renderTo(GuiView view) {
        Gui gui = view.getGui();
        cleanup(view);

        for (PagedLayer<GuiElement> page : pages) {
            page.renderTo(view);
        }
        for (Map.Entry<SlotPosition, GuiElement> entry : items.entrySet()) {
            gui.getElements().put(entry.getKey(), entry.getValue());
        }
    }

    private GuiElement createButton(List<ItemStack> stacks, GuiInfo info, boolean animated) {
        if (animated) {
            return new GuiAnimatedButton((gView, currentTick) -> {
                if (lastTime == 0) lastTime = currentTick;
                int elapsed = currentTick - lastTime;

                if (stacks.isEmpty()) return null;
                int index = (elapsed / 20) % stacks.size();
                return stacks.get(index);
            }, (gv, type, cursor, current) -> {
                if (!type.isShiftClick() && type.isLeftClick()) {
                    MainMenu.openRecipe(gv, current, info, RecipeViewer.RecipeType.RECIPE);
                } else if (type.isRightClick()) {
                    MainMenu.openRecipe(gv, current, info, RecipeViewer.RecipeType.USE);
                } else if (Input.isShiftLeftClick(type)) {
                    MainMenu.addFavourite(gv.getInventoryView().getPlayer().getUniqueId(), current);
                }
            });
        } else {
            return new GuiButton(stacks.getFirst(), (ctx) -> {
                if (!ctx.clickType().isShiftClick() && ctx.clickType().isLeftClick()) {
                    MainMenu.openRecipe(ctx.view(), stacks.getFirst(), info, RecipeViewer.RecipeType.RECIPE);
                } else if (ctx.clickType().isRightClick()) {
                    MainMenu.openRecipe(ctx.view(), stacks.getFirst(), info, RecipeViewer.RecipeType.USE);
                } else if (Input.isShiftLeftClick(ctx.clickType())) {
                    MainMenu.addFavourite(ctx.view().getInventoryView().getPlayer().getUniqueId(), stacks.getFirst());
                }
            });
        }
    }
}