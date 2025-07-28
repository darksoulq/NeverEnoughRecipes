package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.*;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.GuiButton;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.GuiItem;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.PaginatedElements;
import com.github.darksoulq.abyssallib.world.level.item.Items;
import com.github.darksoulq.ner.data.NamespacedFilterManager;
import com.github.darksoulq.ner.data.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TextUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class MainMenu {
    private static final Map<String, BiFunction<String, ItemStack, Boolean>> FILTERS = new HashMap<>();
    private static final Map<UUID, ItemStack[]> INVENTORY_BACKUPS = new HashMap<>();
    private static final Component TITLE = TextUtil.parse("<white><offset><title></white>",
            Placeholder.parsed("title", Pack.MAIN_MENU.toMiniMessageString()),
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-60)));

    private static final int[] DISPLAY_SLOTS = {
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    static {
        addFilter("@", (input, stack) -> {
            List<String> nm =  NamespacedFilterManager.getMatchingNamespaces(input);
            List<ItemStack> sortedNamespaceItems = NamespacedFilterManager.getAllItemsSorted()
                    .stream()
                    .filter(nsItem -> nm.contains(NamespacedFilterManager.getNamespaceOf(nsItem)))
                    .toList();
            return sortedNamespaceItems.contains(stack);

        });
    }

    public static void addFilter(String key, BiFunction<String, ItemStack, Boolean> filter) {
        FILTERS.put(key.toLowerCase(Locale.ROOT), filter);
    }

    public static Gui create() {
        List<ItemStack> sorted = NamespacedFilterManager.getAllItemsSorted();

        List<GuiElement> elements = new LinkedList<>();
        for (ItemStack v : sorted) {
            elements.add(new GuiButton(v, (view, type) -> {
                if (RecipeManager.getRecipesForResult(v).isEmpty()) return;
                GuiManager.open(view.getInventoryView().getPlayer(), RecipeViewer.create(v));
            }));
        }

        PaginatedElements page = new PaginatedElements(elements, DISPLAY_SLOTS, GuiView.Segment.BOTTOM);
        AtomicReference<String> old = new AtomicReference<>("");

        ItemStack filler = Items.INVISIBLE_ITEM.get().getStack();
        filler.setData(DataComponentTypes.ITEM_NAME, Component.text(""));

        return new Gui.Builder(MenuType.ANVIL, TITLE)
                .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
                .addLayer(page)
                .set(SlotPosition.bottom(0), new GuiButton(UiItems.PREV.get().getStack(),
                        (view, click) -> page.prev(view)))
                .set(SlotPosition.bottom(8), new GuiButton(UiItems.NEXT.get().getStack(),
                        (view, click) -> page.next(view)))
                .set(SlotPosition.top(0), new GuiItem(filler))
                .onOpen(view -> {
                    Player player = (Player) view.getInventoryView().getPlayer();
                    INVENTORY_BACKUPS.put(player.getUniqueId(), player.getInventory().getContents());
                    ItemStack[] clearer = view.getBottom().getContents();
                    Arrays.fill(clearer, ItemStack.empty());
                    view.getBottom().setContents(clearer);
                })
                .onTick(view -> {
                    if (!(view.getInventoryView() instanceof AnvilView anvil)) return;

                    String input = anvil.getRenameText();
                    if (input == null) input = "";
                    if (old.get().equals(input)) return;
                    old.set(input);

                    Predicate<GuiElement> predicate;

                    int prefixIndex = input.indexOf(':');
                    if (prefixIndex > 0) {
                        String prefix = input.substring(0, prefixIndex).toLowerCase(Locale.ROOT);
                        String query = input.substring(prefixIndex + 1);

                        BiFunction<String, ItemStack, Boolean> func = FILTERS.get(prefix);
                        predicate = func != null
                                ? el -> func.apply(query, el.render(view, 0))
                                : createFallbackFilter(input);
                    } else {
                        String query = input.toLowerCase(Locale.ROOT);
                        predicate = createFallbackFilter(query);
                    }

                    page.setFilter(predicate);
                })
                .onClose(view -> {
                    view.getTop().setItem(0, ItemStack.of(Material.AIR));
                    ItemStack[] clearer = view.getBottom().getContents();
                    Arrays.fill(clearer, ItemStack.empty());
                    view.getBottom().setContents(clearer);
                    Player player = (Player) view.getInventoryView().getPlayer();
                    if (INVENTORY_BACKUPS.containsKey(player.getUniqueId())) {
                        player.getInventory().setContents(INVENTORY_BACKUPS.get(player.getUniqueId()));
                    }
                })
                .build();
    }

    private static Predicate<GuiElement> createFallbackFilter(String query) {
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        return el -> {
            ItemStack item = el.render(null, 0);
            if (item == null) return false;
            Component name = item.getData(DataComponentTypes.CUSTOM_NAME);
            if (name == null) name = item.getData(DataComponentTypes.ITEM_NAME);
            if (name == null) return false;
            return PlainTextComponentSerializer.plainText().serialize(name).toLowerCase(Locale.ROOT).contains(lowerQuery);
        };
    }

    public static String getItemDisplayName(ItemStack item) {
        Component name = item.getData(DataComponentTypes.CUSTOM_NAME);
        if (name == null) name = item.getData(DataComponentTypes.ITEM_NAME);
        if (name == null) name = Component.text("");
        return PlainTextComponentSerializer.plainText().serialize(name);
    }
}
