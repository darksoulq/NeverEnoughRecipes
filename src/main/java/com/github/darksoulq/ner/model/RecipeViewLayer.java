package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.*;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.GuiAnimatedItem;
import com.github.darksoulq.ner.data.RecipeManager;
import com.github.darksoulq.ner.gui.RecipeViewer;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeViewLayer implements GuiLayer {
    public final ParsedRecipeView view;
    private final Map<SlotPosition, GuiAnimatedItem> items = new HashMap<>();
    private int lastTime = 0;

    public RecipeViewLayer(ParsedRecipeView view) {
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
            }, (gView, type, cursor, current) -> {
                if (RecipeManager.getAllItems().contains(current)) {
                    GuiManager.open(gView.getInventoryView().getPlayer(), RecipeViewer.create(current));
                }
            });

            items.put(position, animated);
        }
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
