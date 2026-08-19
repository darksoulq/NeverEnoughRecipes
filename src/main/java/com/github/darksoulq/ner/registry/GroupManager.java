package com.github.darksoulq.ner.registry;

import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.model.GuiEntry;
import com.github.darksoulq.ner.model.ItemGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroupManager {
    private static final Map<ItemStack, ItemGroup> ITEMSTACK_TO_GROUP = new ConcurrentHashMap<>();

    public static void clear() {
        ITEMSTACK_TO_GROUP.clear();
    }

    public static void addGroup(ItemGroup group) {
        for (ItemStack item : group.items()) {
            ITEMSTACK_TO_GROUP.put(item, group);
        }
    }

    public static ItemGroup getGroup(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        return ITEMSTACK_TO_GROUP.get(item);
    }

    public static List<GuiEntry> buildEntries(Player player, Set<String> expandedGroups) {
        List<GuiEntry> entries = new ArrayList<>();
        Set<String> seenGroups = new HashSet<>();
        boolean groupsEnabled = NeverEnoughRecipes.CONFIG.enableItemGroups.get();

        for (ItemStack item : IngredientManager.getItems()) {
            if (!IngredientManager.isVisible(player, item)) continue;

            if (groupsEnabled) {
                ItemGroup group = getGroup(item);
                if (group != null) {
                    if (seenGroups.add(group.id())) {
                        if (expandedGroups.contains(group.id())) {
                            entries.add(new GuiEntry.GroupCollapseEntry(group));
                            for (ItemStack groupItem : group.items()) {
                                if (IngredientManager.isVisible(player, groupItem)) {
                                    entries.add(new GuiEntry.ItemEntry(groupItem));
                                }
                            }
                        } else {
                            entries.add(new GuiEntry.GroupEntry(group));
                        }
                    }
                } else {
                    entries.add(new GuiEntry.ItemEntry(item));
                }
            } else {
                entries.add(new GuiEntry.ItemEntry(item));
            }
        }
        return entries;
    }
}