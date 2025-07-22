package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class ParsedRecipeView {
    private final Map<Integer, List<ItemStack>> slotMap;
    private final Font.TextureGlyph texture;
    private final int offset;
    private final ItemStack provider;

    public ParsedRecipeView(Map<Integer, List<ItemStack>> slotMap, Font.TextureGlyph texture, int offset,
                            @Nullable ItemStack provider) {
        this.slotMap = Map.copyOf(slotMap);
        this.texture = texture;
        this.offset = offset;
        this.provider = provider;
    }

    public Map<Integer, List<ItemStack>> getSlotMap() {
        return slotMap;
    }

    public Font.TextureGlyph getTexture() {
        return texture;
    }

    public int getOffset() {
        return offset;
    }

    public ItemStack getProvider() {
        return provider;
    }
}
