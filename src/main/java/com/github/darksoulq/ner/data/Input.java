package com.github.darksoulq.ner.data;

import org.bukkit.event.inventory.ClickType;

public class Input {
    public static boolean isShiftLeftClick(ClickType type) {
        return type.isShiftClick() && type.isLeftClick();
    }
    public static boolean isShiftRightClick(ClickType type) {
        return type.isShiftClick() && type.isLeftClick();
    }
}
