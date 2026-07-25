package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiHistory {
    private static final Map<UUID, Deque<Runnable>> HISTORY = new HashMap<>();

    public static void push(Player player, Runnable state) {
        HISTORY.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>()).push(state);
    }

    public static void back(Player player, GuiView currentView) {
        InventoryBackupManager.transition(currentView);
        Deque<Runnable> stack = HISTORY.get(player.getUniqueId());
        if (stack != null && !stack.isEmpty()) {
            stack.pop().run();
        } else {
            GuiManager.open(player, MainMenu.create(player));
        }
    }

    public static void clear(Player player) {
        HISTORY.remove(player.getUniqueId());
    }
}