package com.github.darksoulq.ner.gui.layer;

import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;

import java.util.List;
import java.util.function.BiFunction;

public class DynamicPagedLayer<T> extends PagedLayer<T> {

    public DynamicPagedLayer(List<T> source, int[] slots, GuiView.Segment segment, BiFunction<T, Integer, GuiElement> mapper) {
        super(source, slots, segment, mapper);
    }

    @Override
    public void next(GuiView view) {
        if (this.page < getPageCount() - 1) {
            this.page++;
            invalidate();
            cleanup(view);
            renderTo(view);
        }
    }

    @Override
    public void previous(GuiView view) {
        if (this.page > 0) {
            this.page--;
            invalidate();
            cleanup(view);
            renderTo(view);
        }
    }

    public void updateSource(List<T> newSource, GuiView view) {
        this.source.clear();
        this.source.addAll(newSource);
        this.filtered = this.source.stream().filter(this.filter).toList();

        int maxPage = Math.max(0, getPageCount() - 1);
        if (this.page > maxPage) {
            this.page = maxPage;
        }

        invalidate();
        cleanup(view);
        renderTo(view);
    }
}