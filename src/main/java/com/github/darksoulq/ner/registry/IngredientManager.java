package com.github.darksoulq.ner.registry;

import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.model.ItemGroup;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
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
    private static final Map<ItemStack, String> ITEM_SEARCH_CACHE = new ConcurrentHashMap<>();
    private static final List<Function<ItemStack, ItemStack>> DEDUPLICATORS = new ArrayList<>();
    private static final List<BiFunction<Player, ItemStack, ItemStack>> MODIFIERS = new ArrayList<>();

    private static final Map<String, Comparator<Either<ItemStack, ItemGroup>>> NAMESPACE_COMPARATORS = new ConcurrentHashMap<>();
    private static final Map<ItemStack, Integer> CUSTOM_ORDER = new ConcurrentHashMap<>();
    private static final AtomicInteger ORDER_COUNTER = new AtomicInteger(0);

    private static List<ItemStack> CACHED_SORTED_ITEMS = null;

    public static void clear() {
        FILTERS.clear();
        ALL_ITEMS.clear();
        HIDDEN_ITEMS.clear();
        ITEM_NAMESPACES.clear();
        ITEM_SEARCH_CACHE.clear();
        DEDUPLICATORS.clear();
        MODIFIERS.clear();
        NAMESPACE_COMPARATORS.clear();
        CUSTOM_ORDER.clear();
        ORDER_COUNTER.set(0);
        CACHED_SORTED_ITEMS = null;
    }

    public static void addFilter(String prefix, BiFunction<String, ItemStack, Boolean> filter) {
        FILTERS.put(prefix, filter);
    }

    public static void addDeduplicator(Function<ItemStack, ItemStack> deduplicator) {
        DEDUPLICATORS.add(deduplicator);
    }

    public static void addModifier(BiFunction<Player, ItemStack, ItemStack> modifier) {
        MODIFIERS.add(modifier);
    }

    public static void setNamespaceComparator(String namespace, Comparator<Either<ItemStack, ItemGroup>> comparator) {
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

    public static ItemStack applyModifiers(Player player, ItemStack item) {
        if (item == null || item.isEmpty()) return item;
        ItemStack current = item;
        for (BiFunction<Player, ItemStack, ItemStack> func : MODIFIERS) {
            current = func.apply(player, current);
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

            Component comp = normalized.hasData(DataComponentTypes.CUSTOM_NAME) ? normalized.getData(DataComponentTypes.CUSTOM_NAME) : (normalized.hasData(DataComponentTypes.ITEM_NAME) ? normalized.getData(DataComponentTypes.ITEM_NAME) : Component.text(normalized.getType().name()));
            ITEM_SEARCH_CACHE.put(normalized, PlainTextComponentSerializer.plainText().serialize(comp).toLowerCase(Locale.ROOT));

            CACHED_SORTED_ITEMS = null;
        }
    }

    public static void addItem(String namespace, ItemStack item) {
        if (item == null || item.isEmpty()) return;
        ItemStack normalized = deduplicate(item.asOne());
        if (!HIDDEN_ITEMS.contains(normalized)) {
            ALL_ITEMS.add(normalized);
            ITEM_NAMESPACES.put(normalized, namespace);
            CUSTOM_ORDER.putIfAbsent(normalized, ORDER_COUNTER.getAndIncrement());

            Component comp = normalized.hasData(DataComponentTypes.CUSTOM_NAME) ? normalized.getData(DataComponentTypes.CUSTOM_NAME) : (normalized.hasData(DataComponentTypes.ITEM_NAME) ? normalized.getData(DataComponentTypes.ITEM_NAME) : Component.text(normalized.getType().name()));
            ITEM_SEARCH_CACHE.put(normalized, PlainTextComponentSerializer.plainText().serialize(comp).toLowerCase(Locale.ROOT));

            CACHED_SORTED_ITEMS = null;
        }
    }

    public static void removeItem(ItemStack item) {
        if (item == null || item.isEmpty()) return;
        ItemStack normalized = deduplicate(item.asOne());
        HIDDEN_ITEMS.add(normalized);
        ALL_ITEMS.remove(normalized);
        ITEM_NAMESPACES.remove(normalized);
        ITEM_SEARCH_CACHE.remove(normalized);
        CUSTOM_ORDER.remove(normalized);
        CACHED_SORTED_ITEMS = null;
    }

    public static void removeItems(Predicate<ItemStack> predicate) {
        boolean removed = ALL_ITEMS.removeIf(item -> {
            if (predicate.test(item)) {
                HIDDEN_ITEMS.add(item);
                ITEM_NAMESPACES.remove(item);
                ITEM_SEARCH_CACHE.remove(item);
                CUSTOM_ORDER.remove(item);
                return true;
            }
            return false;
        });
        if (removed) CACHED_SORTED_ITEMS = null;
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
        boolean groupsEnabled = NeverEnoughRecipes.CONFIG.enableItemGroups.get();

        items.sort((a, b) -> {
            ItemGroup groupA = groupsEnabled ? GroupManager.getGroup(a) : null;
            ItemGroup groupB = groupsEnabled ? GroupManager.getGroup(b) : null;

            if (groupA != null && groupB != null && groupA.id().equals(groupB.id())) {
                return Integer.compare(groupA.items().indexOf(a), groupB.items().indexOf(b));
            }

            ItemStack reprA = groupA != null && !groupA.items().isEmpty() ? groupA.items().getFirst() : a;
            ItemStack reprB = groupB != null && !groupB.items().isEmpty() ? groupB.items().getFirst() : b;

            String nsA = getNamespace(reprA);
            String nsB = getNamespace(reprB);

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

            Comparator<Either<ItemStack, ItemGroup>> customComparator = NAMESPACE_COMPARATORS.get(nsA);
            if (customComparator != null) {
                Either<ItemStack, ItemGroup> eitherA = groupA != null ? Either.right(groupA) : Either.left(a);
                Either<ItemStack, ItemGroup> eitherB = groupB != null ? Either.right(groupB) : Either.left(b);
                int result = customComparator.compare(eitherA, eitherB);
                if (result != 0) return result;
            } else {
                String nameA = ITEM_SEARCH_CACHE.getOrDefault(reprA, "");
                String nameB = ITEM_SEARCH_CACHE.getOrDefault(reprB, "");
                int nameCompare = nameA.compareToIgnoreCase(nameB);
                if (nameCompare != 0) return nameCompare;
            }

            int orderA = CUSTOM_ORDER.getOrDefault(reprA, Integer.MAX_VALUE);
            int orderB = CUSTOM_ORDER.getOrDefault(reprB, Integer.MAX_VALUE);
            return Integer.compare(orderA, orderB);
        });
    }

    public static List<ItemStack> getItems() {
        if (CACHED_SORTED_ITEMS == null) {
            List<ItemStack> list = new ArrayList<>(ALL_ITEMS);
            sortItems(list);
            CACHED_SORTED_ITEMS = list;
        }
        return CACHED_SORTED_ITEMS;
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
            results = ALL_ITEMS.stream().filter(item -> ITEM_SEARCH_CACHE.getOrDefault(item, "").contains(lowerQuery)).collect(Collectors.toList());
        }

        sortItems(results);
        return results;
    }
}