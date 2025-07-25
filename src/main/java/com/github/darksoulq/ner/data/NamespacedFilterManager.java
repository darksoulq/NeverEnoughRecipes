package com.github.darksoulq.ner.data;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NamespacedFilterManager {
    private static final Map<String, List<ItemStack>> NAMESPACED_ITEMS = new HashMap<>();

    public static void addItem(String namespace, ItemStack item) {
        namespace = namespace.toLowerCase(Locale.ROOT);
        NAMESPACED_ITEMS.computeIfAbsent(namespace, k -> new ArrayList<>()).add(item);
    }

    public static List<String> getMatchingNamespaces(String partialNamespace) {
        partialNamespace = partialNamespace.toLowerCase(Locale.ROOT);
        List<String> matching = new ArrayList<>();
        for (String key : NAMESPACED_ITEMS.keySet()) {
            if (key.contains(partialNamespace)) {
                matching.add(key);
            }
        }
        return matching;
    }

    public static List<ItemStack> getItemsForNamespaces(List<String> namespaces) {
        List<ItemStack> result = new ArrayList<>();
        for (String ns : namespaces) {
            List<ItemStack> items = NAMESPACED_ITEMS.get(ns.toLowerCase(Locale.ROOT));
            if (items != null) result.addAll(items);
        }
        return result;
    }
}
