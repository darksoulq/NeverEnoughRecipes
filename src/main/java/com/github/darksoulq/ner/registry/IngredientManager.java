package com.github.darksoulq.ner.registry;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IngredientManager {
    private static final Map<String, BiFunction<String, ItemStack, Boolean>> FILTERS = new ConcurrentHashMap<>();
    private static final Set<ItemStack> ALL_ITEMS = ConcurrentHashMap.newKeySet();
    private static final Set<ItemStack> HIDDEN_ITEMS = ConcurrentHashMap.newKeySet();
    private static final Map<ItemStack, String> ITEM_NAMESPACES = new ConcurrentHashMap<>();
    private static final List<Function<ItemStack, ItemStack>> DEDUPLICATORS = new ArrayList<>();
    private static final List<Function<ItemStack, ItemStack>> MODIFIERS = new ArrayList<>();

    private static final Map<String, Comparator<ItemStack>> NAMESPACE_COMPARATORS = new ConcurrentHashMap<>();
    private static final Map<ItemStack, Integer> CUSTOM_ORDER = new ConcurrentHashMap<>();
    private static final AtomicInteger ORDER_COUNTER = new AtomicInteger(0);

    public static void clear() {
        FILTERS.clear();
        ALL_ITEMS.clear();
        HIDDEN_ITEMS.clear();
        ITEM_NAMESPACES.clear();
        DEDUPLICATORS.clear();
        MODIFIERS.clear();
        NAMESPACE_COMPARATORS.clear();
        CUSTOM_ORDER.clear();
        ORDER_COUNTER.set(0);
    }

    public static void addFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter) {
        FILTERS.put(prefix, filter);
    }

    public static void addDeduplicator(Function<ItemStack, ItemStack> deduplicator) {
        DEDUPLICATORS.add(deduplicator);
    }

    public static void addModifier(Function<ItemStack, ItemStack> modifier) {
        MODIFIERS.add(modifier);
    }

    public static void setNamespaceComparator(String namespace, Comparator<ItemStack> comparator) {
        NAMESPACE_COMPARATORS.put(namespace.toLowerCase(Locale.ROOT), comparator);
    }

    public static ItemStack deduplicate(ItemStack item) {
        if (item == null || item.isEmpty()) return item;
        ItemStack current = item;
        for (Function<ItemStack, ItemStack> func : DEDUPLICATORS) {
            current = func.apply(current);
        }
        return current;
    }

    public static ItemStack applyModifiers(ItemStack item) {
        if (item == null || item.isEmpty()) return item;
        ItemStack current = item;
        for (Function<ItemStack, ItemStack> func : MODIFIERS) {
            current = func.apply(current);
        }
        return current;
    }

    public static void addItem(ItemStack item) {
        if (item == null || item.isEmpty()) return;
        ItemStack normalized = deduplicate(item.asOne());
        if (!HIDDEN_ITEMS.contains(normalized)) {
            ALL_ITEMS.add(normalized);
            String ns = normalized.getType().getKey().getNamespace();
            ITEM_NAMESPACES.putIfAbsent(normalized, ns);
            CUSTOM_ORDER.putIfAbsent(normalized, ORDER_COUNTER.getAndIncrement());
        }
    }

    public static void addItem(String namespace, ItemStack item) {
        if (item == null || item.isEmpty()) return;
        ItemStack normalized = deduplicate(item.asOne());
        if (!HIDDEN_ITEMS.contains(normalized)) {
            ALL_ITEMS.add(normalized);
            ITEM_NAMESPACES.put(normalized, namespace);
            CUSTOM_ORDER.putIfAbsent(normalized, ORDER_COUNTER.getAndIncrement());
        }
    }

    public static void removeItem(ItemStack item) {
        if (item == null || item.isEmpty()) return;
        ItemStack normalized = deduplicate(item.asOne());
        HIDDEN_ITEMS.add(normalized);
        ALL_ITEMS.remove(normalized);
        ITEM_NAMESPACES.remove(normalized);
        CUSTOM_ORDER.remove(normalized);
    }

    public static void removeItems(Predicate<ItemStack> predicate) {
        ALL_ITEMS.removeIf(item -> {
            if (predicate.test(item)) {
                HIDDEN_ITEMS.add(item);
                ITEM_NAMESPACES.remove(item);
                CUSTOM_ORDER.remove(item);
                return true;
            }
            return false;
        });
    }

    public static boolean isHidden(ItemStack item) {
        return HIDDEN_ITEMS.contains(deduplicate(item.asOne()));
    }

    public static String getNamespace(ItemStack item) {
        if (item == null || item.isEmpty()) return "unknown";
        String ns = ITEM_NAMESPACES.get(deduplicate(item.asOne()));
        if (ns == null) ns = item.getType().getKey().getNamespace();
        return ns.isBlank() ? "unknown" : ns;
    }

    public static void sortItems(List<ItemStack> items) {
        items.sort((a, b) -> {
            String nsA = getNamespace(a);
            String nsB = getNamespace(b);

            boolean aUnknown = nsA.equals("unknown");
            boolean bUnknown = nsB.equals("unknown");

            if (aUnknown && !bUnknown) return 1;
            if (!aUnknown && bUnknown) return -1;

            boolean aMc = nsA.equals("minecraft");
            boolean bMc = nsB.equals("minecraft");

            if (aMc && !bMc) return -1;
            if (!aMc && bMc) return 1;

            int nsCompare = nsA.compareToIgnoreCase(nsB);
            if (nsCompare != 0) return nsCompare;

            Comparator<ItemStack> customComparator = NAMESPACE_COMPARATORS.get(nsA);
            if (customComparator != null) {
                return customComparator.compare(a, b);
            }

            int orderA = CUSTOM_ORDER.getOrDefault(a, Integer.MAX_VALUE);
            int orderB = CUSTOM_ORDER.getOrDefault(b, Integer.MAX_VALUE);
            return Integer.compare(orderA, orderB);
        });
    }

    public static List<ItemStack> getItems() {
        List<ItemStack> list = new ArrayList<>(ALL_ITEMS);
        sortItems(list);
        return list;
    }

    public static List<ItemStack> search(String query) {
        if (query == null || query.isBlank()) return getItems();

        String lowerQuery = query.toLowerCase(Locale.ROOT);
        List<ItemStack> results = null;

        for (Map.Entry<String, BiFunction<String, ItemStack, Boolean>> entry : FILTERS.entrySet()) {
            String prefix = entry.getKey();
            if (lowerQuery.startsWith(prefix)) {
                String term = lowerQuery.substring(prefix.length());
                results = ALL_ITEMS.stream().filter(item -> entry.getValue().apply(term, item)).collect(Collectors.toList());
                break;
            }
        }

        if (results == null) {
            results = ALL_ITEMS.stream().filter(item -> {
                Component comp = item.hasData(DataComponentTypes.CUSTOM_NAME) ? item.getData(DataComponentTypes.CUSTOM_NAME) : (item.hasData(DataComponentTypes.ITEM_NAME) ? item.getData(DataComponentTypes.ITEM_NAME) : Component.text(item.getType().name()));
                return PlainTextComponentSerializer.plainText().serialize(comp).toLowerCase(Locale.ROOT).contains(lowerQuery);
            }).collect(Collectors.toList());
        }

        sortItems(results);
        return results;
    }
}