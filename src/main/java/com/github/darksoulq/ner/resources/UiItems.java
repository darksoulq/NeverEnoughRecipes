package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.DeferredObject;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import org.bukkit.Material;

public class UiItems {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "ner");

    public static final DeferredObject<Item> NEXT = register("forward");
    public static final DeferredObject<Item> PREV = register("backward");
    public static final DeferredObject<Item> CLOSE = register("close");
    public static final DeferredObject<Item> DEFAULT_BOOK = register("book");

    public static DeferredObject<Item> register(String name) {
        return ITEMS.register(name, id -> new Item(id, Material.PAPER));
    }
}
