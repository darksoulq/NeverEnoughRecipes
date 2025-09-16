package com.github.darksoulq.ner.gui;

import java.util.*;

public class GuiHistory {
    private static final Map<UUID, Deque<GuiInfo>> HISTORY = new HashMap<>();

    public static void push(UUID uuid, GuiInfo info) {
        if (info == null) return;
        HISTORY.computeIfAbsent(uuid, k -> new ArrayDeque<>()).push(info);
    }

    public static GuiInfo pop(UUID uuid) {
        Deque<GuiInfo> stack = HISTORY.get(uuid);
        if (stack == null || stack.isEmpty()) return null;
        GuiInfo info = stack.pop();
        if (stack.isEmpty()) HISTORY.remove(uuid);
        return info;
    }

    public static void clear(UUID uuid) {
        HISTORY.remove(uuid);
    }
}
