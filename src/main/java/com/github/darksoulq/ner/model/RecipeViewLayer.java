package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.*;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.GuiAnimatedItem;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
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

            GuiAnimatedItem animated = GuiAnimatedItem.of((gview, currentTick) -> {
                if (lastTime == 0) lastTime = currentTick;
                int elapsed = currentTick - lastTime;

                if (stackList.isEmpty()) return null;
                int index = (elapsed / 20) % stackList.size();
                return stackList.get(index);
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
