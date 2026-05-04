package com.github.darksoulq.ner.model;

import org.bukkit.event.inventory.ClickType;

public enum ControlAction {
    VIEW_RECIPE(ClickType.LEFT),
    VIEW_USES(ClickType.RIGHT),
    TOGGLE_BOOKMARK(ClickType.SHIFT_LEFT);

    public final ClickType defaultBind;

    ControlAction(ClickType defaultBind) {
        this.defaultBind = defaultBind;
    }
}