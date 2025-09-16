package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiAnimatedItem;
import com.github.darksoulq.ner.data.Input;
import com.github.darksoulq.ner.gui.GuiInfo;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.gui.RecipeViewer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeViewLayer implements GuiLayer {
    public final ParsedRecipeView view;
    private final Map<SlotPosition, GuiAnimatedItem> items = new HashMap<>();
    private int lastTime = 0;

    public RecipeViewLayer(ParsedRecipeView view, GuiInfo info) {
        this.view = view;

        for (Map.Entry<Integer, List<ItemStack>> entry : view.getSlotMap().entrySet()) {
            int slot = entry.getKey();
            List<ItemStack> stackList = entry.getValue();
            SlotPosition position = SlotPosition.top(slot);

            GuiAnimatedButton animated = new GuiAnimatedButton((gView, currentTick) -> {
                if (lastTime == 0) lastTime = currentTick;
                int elapsed = currentTick - lastTime;

                if (stackList.isEmpty()) return null;
                int index = (elapsed / 20) % stackList.size();
                return stackList.get(index);
            }, (gv, type, cursor, current) -> {
                if (!type.isShiftClick() && type.isLeftClick()) {
                    MainMenu.openRecipe(gv, current, info, RecipeViewer.RecipeType.RECIPE);
                } else if (type.isRightClick()) {
                    MainMenu.openRecipe(gv, current, info, RecipeViewer.RecipeType.USE);
                } else if (Input.isShiftLeftClick(type)) {
                    MainMenu.addFavourite(gv.getInventoryView().getPlayer().getUniqueId(), current);
                }
            });

            items.put(position, animated);
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
    }

    @Override
    public void renderTo(GuiView guiView) {
        Gui gui = guiView.getGui();

        for (SlotPosition pos : items.keySet()) {
            gui.getElements().remove(pos);
        }

        for (Map.Entry<SlotPosition, GuiAnimatedItem> entry : items.entrySet()) {
            gui.getElements().put(entry.getKey(), entry.getValue());
        }
    }
}
