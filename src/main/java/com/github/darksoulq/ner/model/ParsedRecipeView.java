package com.github.darksoulq.ner.model;

import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.ner.layout.PaginatedSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class ParsedRecipeView {
    private final Map<Integer, List<ItemStack>> slotMap;
    private final Font.TextureGlyph texture;
    private final int offset;
    private final ItemStack provider;
    private final List<PaginatedSection> sections;

    public ParsedRecipeView(Map<Integer, List<ItemStack>> slotMap, Font.TextureGlyph texture, int offset,
                            @Nullable ItemStack provider) {
        this(slotMap, texture, offset, provider, null);
    }
    public ParsedRecipeView(Map<Integer, List<ItemStack>> slotMap, Font.TextureGlyph texture, int offset,
                            @Nullable ItemStack provider, @Nullable List<PaginatedSection> sections) {
        this.slotMap = Map.copyOf(slotMap);
        this.texture = texture;
        this.offset = offset;
        this.provider = provider;
        this.sections = sections;
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
    public List<PaginatedSection> getSections() {
        return sections;
    }
}
