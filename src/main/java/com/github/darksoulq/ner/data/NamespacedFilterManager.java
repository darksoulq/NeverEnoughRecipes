package com.github.darksoulq.ner.data;

import com.github.darksoulq.ner.gui.MainMenu;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

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

    public static List<ItemStack> getAllItemsSorted() {
        List<String> namespaces = new ArrayList<>(NAMESPACED_ITEMS.keySet());
        Set<ItemStack> allItems = RecipeManager.getAllItems();

        namespaces.sort((a, b) -> {
            if (a.equals("minecraft")) return -1;
            if (b.equals("minecraft")) return 1;
            return a.compareToIgnoreCase(b);
        });

        List<ItemStack> result = new ArrayList<>();
        Set<ItemStack> seen = new HashSet<>();

        for (String ns : namespaces) {
            List<ItemStack> items = NAMESPACED_ITEMS.get(ns);
            if (items == null) continue;

            Stream<ItemStack> sortedStream = ns.equals("minecraft")
                    ? items.stream().sorted(Comparator.comparing(i -> i.getType().ordinal()))
                    : items.stream().sorted(Comparator.comparing(MainMenu::getItemDisplayName, String.CASE_INSENSITIVE_ORDER));

            sortedStream.forEach(item -> {
                if (seen.add(item)) {
                    result.add(item);
                }
            });
        }

        allItems.stream()
                .filter(item -> !seen.contains(item))
                .sorted(Comparator.comparing(MainMenu::getItemDisplayName, String.CASE_INSENSITIVE_ORDER))
                .forEach(result::add);

        return result;
    }

    public static String getNamespaceOf(ItemStack item) {
        for (Map.Entry<String, List<ItemStack>> entry : NAMESPACED_ITEMS.entrySet()) {
            if (entry.getValue().contains(item)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
