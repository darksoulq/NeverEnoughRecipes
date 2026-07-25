package com.github.darksoulq.ner.util;

import com.github.darksoulq.ner.gui.InventoryBackupManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class CraftabilityUtil {
    public static boolean hasIngredients(Player player, List<RecipeChoice> choices) {
        ItemStack[] backup = InventoryBackupManager.getBackup(player.getUniqueId());
        ItemStack[] inv = backup != null ? backup : player.getInventory().getContents();
        ItemStack[] sim = new ItemStack[inv.length];
        
        for (int i = 0; i < inv.length; i++) {
            sim[i] = inv[i] != null ? inv[i].clone() : null;
        }

        for (RecipeChoice choice : choices) {
            if (choice == null || choice.equals(RecipeChoice.empty())) continue;
            boolean found = false;
            
            for (int i = 0; i < sim.length; i++) {
                if (sim[i] != null && !sim[i].isEmpty() && choice.test(sim[i])) {
                    sim[i].setAmount(sim[i].getAmount() - 1);
                    if (sim[i].isEmpty()) sim[i] = null;
                    found = true;
                    break;
                }
            }
            
            if (!found) return false;
        }
        
        return true;
    }
}