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
        ResourcePack pack = new ResourcePack(pl, "ner");
        Namespace ns = pack.namespace("ner");

        ns.icon();

        Texture mainTex = ns.texture("gui/main");
        Texture mainInvTex = ns.texture("gui/main_inv");
        Texture searchTex = ns.texture("gui/search_menu");
        Texture craftTex = ns.texture("gui/crafting");
        Texture cookTex = ns.texture("gui/cooking");
        Texture brewTex = ns.texture("gui/brewing");
        Texture stoneTex = ns.texture("gui/stone_cutter");
        Texture smithTex = ns.texture("gui/smithing");

        Font fn = ns.font("gui", false);
        MAIN_MENU = fn.glyph(mainTex, 222, 13);
        MAIN_MENU_INV = fn.glyph(mainInvTex, 222, 13);
        SEARCH_MENU = fn.glyph(searchTex, 165, 13);
        CRAFTING_TABLE = fn.glyph(craftTex, 222, 13);
        COOKING = fn.glyph(cookTex, 222, 13);
        BREWING = fn.glyph(brewTex, 222, 13);
        STONE_CUTTER = fn.glyph(stoneTex, 222, 13);
        SMITHING = fn.glyph(smithTex, 222, 13);

        createItemDef(ns, "forward");
        createItemDef(ns, "backward");
        createItemDef(ns, "close");
        createItemDef(ns, "xp");
        createItemDef(ns, "book");
        createItemDef(ns, "search");
        createItemDef(ns, "filter");
        createItemDef(ns, "stage");
        createItemDef(ns, "stage_selected");
        createItemDef(ns, "small_prev");
        createItemDef(ns, "small_next");

        ns.mcmeta("item/xp", true);

        Lang ln = ns.lang("en_us", false);
        ln.put("plugin.ner", "NeverEnoughRecipes");
        ln.put("item.ner.forward", "Forward");
        ln.put("item.ner.backward", "Backward");
        ln.put("item.ner.small_prev", "Previous");
        ln.put("item.ner.small_next", "Next");
        ln.put("item.ner.stage", "Stage");
        ln.put("item.ner.stage_selected", "Stage");
        ln.put("item.ner.close", "Close");
        ln.put("item.ner.xp", "Experience");
        ln.put("item.ner.book", "Never Enough Recipes");
        ln.put("item.ner.search", "Search");
        ln.put("item.ner.filter", "Mode");

        pack.register(false);
    }

    private static void createItemDef(Namespace ns, String name) {
        Texture tex = ns.texture("item/" + name);
        Model mod = ns.model(name, false);
        mod.parent("minecraft:item/generated");
        mod.texture("layer0", tex);
        ns.itemDefinition(name, new Selector.Model(mod), true);
    }
}