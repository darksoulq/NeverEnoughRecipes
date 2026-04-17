package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.element.GuiAnimatedItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class GuiAnimatedButton extends GuiAnimatedItem {
    private final QuadConsumer<GuiView, ClickType, ItemStack, ItemStack> clickHandler;
    public GuiAnimatedButton(BiFunction<GuiView, Integer, ItemStack> renderer, QuadConsumer<GuiView, ClickType, ItemStack, ItemStack> click) {
        super(renderer);
        clickHandler = click;
    }

    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        clickHandler.accept(ctx.view(), ctx.clickType(), ctx.cursor(), ctx.currentItem());
        return ActionResult.CANCEL;
    }

    @FunctionalInterface
    public interface QuadConsumer<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }
}
