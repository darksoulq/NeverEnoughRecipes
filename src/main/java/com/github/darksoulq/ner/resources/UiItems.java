package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.item.Item;
import org.bukkit.Material;

public class UiItems {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "ner");

    public static final Holder<Item> NEXT = register("forward");
    public static final Holder<Item> PREV = register("backward");
    public static final Holder<Item> CLOSE = register("close");
    public static final Holder<Item> SEARCH = register("search");
    public static final Holder<Item> XP = register("xp");
    public static final Holder<Item> FILTER = register("filter");
    public static final Holder<Item> DEFAULT_BOOK = register("book");

    public static Holder<Item> register(String name) {
        return ITEMS.register(name, id -> new Item(id, Material.PAPER));
    }
}
