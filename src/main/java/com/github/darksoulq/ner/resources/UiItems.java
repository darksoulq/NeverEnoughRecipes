package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.item.Item;
import org.bukkit.Material;

public class UiItems {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "ner");

    public static final Item NEXT = register("forward");
    public static final Item PREV = register("backward");
    public static final Item CLOSE = register("close");
    public static final Item SEARCH = register("search");
    public static final Item XP = register("xp");
    public static final Item FILTER = register("filter");
    public static final Item DEFAULT_BOOK = register("book");

    public static Item register(String name) {
        return ITEMS.register(name, id -> new Item(id, Material.PAPER));
    }
}
