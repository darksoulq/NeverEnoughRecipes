package com.github.darksoulq.ner.gui.element;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.world.gui.element.GuiAnimatedItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class GuiAnimatedButton extends GuiAnimatedItem {
    private final Consumer<GuiClickContext> action;

    public GuiAnimatedButton(List<ItemStack> frames, int interval, Consumer<GuiClickContext> action) {
        super((_, tick) -> {
            int safeInterval = Math.max(1, interval);
            int index = (int) ((tick / safeInterval) % frames.size());
            return frames.get(index);
        });
        this.action = action;
    }

    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        action.accept(ctx);
        return ActionResult.CANCEL;
    }
}