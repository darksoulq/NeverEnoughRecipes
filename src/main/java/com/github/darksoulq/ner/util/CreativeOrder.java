package com.github.darksoulq.ner.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.bukkit.Material;

import java.util.*;

public class CreativeOrder {

    private static final Map<Material, Integer> creativeOrderMap = new HashMap<>();

    public static void loadCreativeOrder(MinecraftServer server) {
        List<Item> orderedItems = new ArrayList<>();
        Set<Item> seen = new HashSet<>();

        for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
            if (!tab.equals(CreativeModeTabs.searchTab())) continue;
            tab.getDisplayItems().forEach(i -> {
                if (seen.add(i.getItem())) {
                    orderedItems.add(i.getItem());
                }
            });
        }

        for (int i = 0; i < orderedItems.size(); i++) {
            Item item = orderedItems.get(i);
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            Material material = Material.matchMaterial(key.getPath().toUpperCase(Locale.ROOT));
            if (material != null) {
                creativeOrderMap.put(material, i);
            }
        }
    }

    public static int getCreativeOrder(Material material) {
        return creativeOrderMap.getOrDefault(material, Integer.MAX_VALUE);
    }
}