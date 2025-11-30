package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiAnimatedItem;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.impl.PaginatedElements;
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
    private final List<PaginatedElements> pages = new ArrayList<>();
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
            PaginatedElements page = new PaginatedElements(elements, section.getSlots(), GuiView.Segment.TOP);
            pages.add(page);

            items.put(SlotPosition.top(section.getNextButton().slot()), GuiButton.of(section.getNextButton().display(),
                    (gView, click) -> page.next(gView)));
            items.put(SlotPosition.top(section.getPrevButton().slot()), GuiButton.of(section.getPrevButton().display(),
                    (gView, click) -> page.prev(gView)));
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
        for (PaginatedElements elements : pages) {
            elements.cleanup(view);
            elements.resetPage();
        }
    }

    @Override
    public void renderTo(GuiView view) {
        Gui gui = view.getGui();
        cleanup(view);

        for (PaginatedElements element : pages) {
            element.renderTo(view);
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
            return new GuiButton(stacks.getFirst(), (gv, type) -> {
                if (!type.isShiftClick() && type.isLeftClick()) {
                    MainMenu.openRecipe(gv, stacks.getFirst(), info, RecipeViewer.RecipeType.RECIPE);
                } else if (type.isRightClick()) {
                    MainMenu.openRecipe(gv, stacks.getFirst(), info, RecipeViewer.RecipeType.USE);
                } else if (Input.isShiftLeftClick(type)) {
                    MainMenu.addFavourite(gv.getInventoryView().getPlayer().getUniqueId(), stacks.getFirst());
                }
            });
        }
    }
}
