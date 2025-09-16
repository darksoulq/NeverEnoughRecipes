package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Lang;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;
import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.github.darksoulq.ner.NeverEnoughRecipes;

public class Pack {
    public static Font.TextureGlyph MAIN_MENU;
    public static Font.TextureGlyph MAIN_MENU_INV;
    public static Font.TextureGlyph SEARCH_MENU;
    public static Font.TextureGlyph CRAFTING_TABLE;
    public static Font.TextureGlyph COOKING;
    public static Font.TextureGlyph BREWING;
    public static Font.TextureGlyph STONE_CUTTER;
    public static Font.TextureGlyph SMITHING;

    public static void init(NeverEnoughRecipes pl) {
        ResourcePack pack = new ResourcePack(pl, NeverEnoughRecipes.MODID);
        Namespace ns = pack.namespace("ner");

        Texture MAIN_MENU_TEXTURE = ns.texture("gui/main");
        Texture MAIN_MENU_INV_TEXTURE = ns.texture("gui/main_inv");
        Texture SEARCH_MENU_TEXTURE = ns.texture("gui/search_menu");
        Texture CRAFTING_TABLE_TEXTURE = ns.texture("gui/crafting");
        Texture COOKING_TEXTURE = ns.texture("gui/cooking");
        Texture BREWING_TEXTURE = ns.texture("gui/brewing");
        Texture STONE_CUTTER_TEXTURE = ns.texture("gui/stone_cutter");
        Texture SMITHING_TEXTURE = ns.texture("gui/smithing");

        Font fn = ns.font("gui", false);
        MAIN_MENU = fn.glyph(MAIN_MENU_TEXTURE, 222, 13);
        MAIN_MENU_INV = fn.glyph(MAIN_MENU_INV_TEXTURE, 222, 13);
        SEARCH_MENU = fn.glyph(SEARCH_MENU_TEXTURE, 165, 13);
        CRAFTING_TABLE = fn.glyph(CRAFTING_TABLE_TEXTURE, 222, 13);
        COOKING = fn.glyph(COOKING_TEXTURE, 222, 13);
        BREWING = fn.glyph(BREWING_TEXTURE, 222, 13);
        STONE_CUTTER = fn.glyph(STONE_CUTTER_TEXTURE, 222, 13);
        SMITHING = fn.glyph(SMITHING_TEXTURE, 222, 13);

        // Items
        createItemDef(ns, "forward");
        createItemDef(ns, "backward");
        createItemDef(ns, "close");
        createItemDef(ns, "xp");
        createItemDef(ns, "book");
        createItemDef(ns, "search");
        createItemDef(ns, "filter");

        // Lang
        Lang ln = ns.lang("en_us", false);
        ln.put("item.ner.forward", "Forward");
        ln.put("item.ner.backward", "Backward");
        ln.put("item.ner.close", "Close");
        ln.put("item.ner.xp", "Experience");
        ln.put("item.ner.book", "Never Enough Recipes");
        ln.put("item.ner.search", "Search");
        ln.put("item.ner.filter", "Mode");

        ln.put("lore.ner.recent", "Recents");
        ln.put("lore.ner.inventory", "Inventory");
        ln.put("lore.ner.favourite", "Favourites");

        pack.register(false);
    }

    private static void createItemDef(Namespace ns, String name) {
        Texture tex = ns.texture("item/" + name);
        Model mod = ns.model(name, false);
        mod.parent("minecraft:item/generated");
        mod.texture("layer0", tex);
        Selector.Model sel = new Selector.Model(mod);
        ns.itemDefinition(name, sel, true);
    }
}
