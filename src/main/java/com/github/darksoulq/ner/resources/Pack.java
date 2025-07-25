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
    public static Texture MAIN_MENU_TEXTURE;
    public static Texture CRAFTING_TABLE_TEXTURE;
    public static Texture COOKING_TEXTURE;
    public static Texture BREWING_TEXTURE;
    public static Texture ONE_SLOT_TEXTURE;
    public static Texture THREE_SLOT_TEXTURE;

    public static Font.TextureGlyph MAIN_MENU;
    public static Font.TextureGlyph CRAFTING_TABLE;
    public static Font.TextureGlyph COOKING;
    public static Font.TextureGlyph BREWING;
    public static Font.TextureGlyph ONE_SLOT;
    public static Font.TextureGlyph THREE_SLOT;

    public static void init(NeverEnoughRecipes pl) {
        ResourcePack pack = new ResourcePack(pl, NeverEnoughRecipes.MODID);
        Namespace ns = pack.namespace("ner");

        MAIN_MENU_TEXTURE = ns.texture("gui/main_menu");
        CRAFTING_TABLE_TEXTURE = ns.texture("gui/crafting_table");
        COOKING_TEXTURE = ns.texture("gui/cooking");
        BREWING_TEXTURE = ns.texture("gui/brewing_stand");
        ONE_SLOT_TEXTURE = ns.texture("gui/one_slot");
        THREE_SLOT_TEXTURE = ns.texture("gui/three_slot");

        Font fn = ns.font("gui", false);
        MAIN_MENU = fn.glyph(MAIN_MENU_TEXTURE, 165, 13);
        CRAFTING_TABLE = fn.glyph(CRAFTING_TABLE_TEXTURE, 128, 13);
        COOKING = fn.glyph(COOKING_TEXTURE, 128, 13);
        BREWING = fn.glyph(BREWING_TEXTURE, 128, 13);
        ONE_SLOT = fn.glyph(ONE_SLOT_TEXTURE, 128, 13);
        THREE_SLOT = fn.glyph(THREE_SLOT_TEXTURE, 128, 13);

        // Items
        Texture next = ns.texture("item/forward");
        Texture prev = ns.texture("item/backward");
        Texture close = ns.texture("item/close");

        Model nextModel = ns.model("forward", false);
        nextModel.parent("minecraft:item/generated");
        nextModel.texture("layer0", next);
        Model prevModel = ns.model("backward", false);
        prevModel.parent("minecraft:item/generated");
        prevModel.texture("layer0", prev);
        Model closeModel = ns.model("close", false);
        closeModel.parent("minecraft:item/generated");
        closeModel.texture("layer0", close);

        Selector.Model nextSel = new Selector.Model(nextModel);
        Selector.Model prevSel = new Selector.Model(prevModel);
        Selector.Model closeSel = new Selector.Model(closeModel);

        ns.itemDefinition("forward", nextSel, false);
        ns.itemDefinition("backward", prevSel, false);
        ns.itemDefinition("close", closeSel, false);

        // Lang
        Lang ln = ns.lang("en_us", false);
        ln.put("item.ner.forward", "Forward");
        ln.put("item.ner.backward", "Backward");
        ln.put("item.ner.close", "Close");
        ln.put("item.ner.book", "Never Enough Recipes");

        pack.register(false);
    }
}
