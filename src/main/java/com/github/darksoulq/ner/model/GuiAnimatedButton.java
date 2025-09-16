package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiAnimatedItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class GuiAnimatedButton extends GuiAnimatedItem {
    private QuadConsumer<GuiView, ClickType, ItemStack, ItemStack> clickHandler;
    public GuiAnimatedButton(BiFunction<GuiView, Integer, ItemStack> renderer, QuadConsumer<GuiView, ClickType, ItemStack, ItemStack> click) {
        super(renderer);
        clickHandler = click;
    }

    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        clickHandler.accept(view, click, cursor, current);
        return ActionResult.CANCEL;
    }

    @FunctionalInterface
    public interface QuadConsumer<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }
}
