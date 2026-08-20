package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryBackupManager {
    private static final Map<UUID, ItemStack[]> BACKUPS = new HashMap<>();

    public static void save(Player player) {
        if (!BACKUPS.containsKey(player.getUniqueId())) {
            BACKUPS.put(player.getUniqueId(), player.getInventory().getContents().clone());
        }
    }

    public static void setup(GuiView view) {
        save(view.getPlayer());

        view.getBottom().clear();
        view.getTop().setItem(0, ItemStack.of(Material.AIR));
    }

    public static void restore(GuiView view) {
        view.getTop().setItem(0, ItemStack.of(Material.AIR));

        Player player = view.getPlayer();
        if (!GuiManager.OPEN_VIEWS.containsKey(view.getInventoryView())) {
            ItemStack[] backup = BACKUPS.remove(player.getUniqueId());
            if (backup != null) {
                player.getInventory().setContents(backup);
            }
        }
    }

    public static void transition(GuiView view) {
        view.getTop().clear();
        GuiManager.remove(view);
    }

    public static ItemStack[] getBackup(UUID uuid) {
        return BACKUPS.get(uuid);
    }
}