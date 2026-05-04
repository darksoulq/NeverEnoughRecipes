package com.github.darksoulq.ner.plugin;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.ner.layout.impl.*;
import com.github.darksoulq.ner.registry.IngredientManager;
import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class VanillaNerPlugin implements NerPlugin {

    private static final Map<Material, Integer> CREATIVE_ORDER = new EnumMap<>(Material.class);
    private static boolean creativeOrderInitialized = false;

    private static int getCreativeOrder(Material material) {
        if (!creativeOrderInitialized) {
            int order = 0;
            try {
                for (net.minecraft.world.item.CreativeModeTab tab : net.minecraft.world.item.CreativeModeTabs.allTabs()) {
                    if (tab.getType() == net.minecraft.world.item.CreativeModeTab.Type.CATEGORY) {
                        for (net.minecraft.world.item.ItemStack nmsItem : tab.getDisplayItems()) {
                            Material mat = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nmsItem).getType();
                            CREATIVE_ORDER.putIfAbsent(mat, order++);
                        }
                    }
                }
            } catch (Throwable ignored) {}

            if (CREATIVE_ORDER.isEmpty()) {
                for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
                    Material mat = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(new net.minecraft.world.item.ItemStack(item)).getType();
                    CREATIVE_ORDER.putIfAbsent(mat, order++);
                }
            }
            creativeOrderInitialized = true;
        }
        return CREATIVE_ORDER.getOrDefault(material, Integer.MAX_VALUE);
    }

    @Override
    public void register(Registration registry) {
        registry.addDeduplicator(item -> {
            if (item.getType() == Material.SUSPICIOUS_STEW) {
                return new ItemStack(Material.SUSPICIOUS_STEW);
            }
            return item;
        });

        registry.setNamespaceComparator("minecraft", (a, b) -> {
            int orderA = getCreativeOrder(a.getType());
            int orderB = getCreativeOrder(b.getType());
            return Integer.compare(orderA, orderB);
        });

        registry.addFilter("@", (term, item) -> {
            String ns = IngredientManager.getNamespace(item);
            return ns.toLowerCase(Locale.ROOT).contains(term);
        });

        registry.addFilter(":", (term, item) -> {
            if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
            for (Component line : item.getItemMeta().lore()) {
                if (PlainTextComponentSerializer.plainText().serialize(line).toLowerCase(Locale.ROOT).contains(term)) {
                    return true;
                }
            }
            return false;
        });

        registry.addFilter("#", (term, item) -> {
            for (Map.Entry<String, Tag<?, ?>> entry : Registries.TAGS.getAll().entrySet()) {
                if (entry.getValue() instanceof ItemTag itemTag) {
                    if (entry.getKey().toLowerCase(Locale.ROOT).contains(term) && itemTag.contains(item)) {
                        return true;
                    }
                }
            }
            return false;
        });

        registry.addCategory(new ShapedCategory());
        registry.addCatalyst(ShapedRecipe.class, new ItemStack(Material.CRAFTING_TABLE));

        registry.addCategory(new ShapelessCategory());
        registry.addCatalyst(ShapelessRecipe.class, new ItemStack(Material.CRAFTING_TABLE));

        registry.addCategory(new TransmuteCategory());
        registry.addCatalyst(TransmuteRecipe.class, new ItemStack(Material.CRAFTING_TABLE));

        registry.addCategory(new CookingCategory<>(FurnaceRecipe.class));
        registry.addCatalyst(FurnaceRecipe.class, new ItemStack(Material.FURNACE));

        registry.addCategory(new CookingCategory<>(BlastingRecipe.class));
        registry.addCatalyst(BlastingRecipe.class, new ItemStack(Material.BLAST_FURNACE));

        registry.addCategory(new CookingCategory<>(SmokingRecipe.class));
        registry.addCatalyst(SmokingRecipe.class, new ItemStack(Material.SMOKER));

        registry.addCategory(new CookingCategory<>(CampfireRecipe.class));
        registry.addCatalyst(CampfireRecipe.class, new ItemStack(Material.CAMPFIRE));

        registry.addCategory(new SmithingTransformCategory());
        registry.addCatalyst(SmithingTransformRecipe.class, new ItemStack(Material.SMITHING_TABLE));

        registry.addCategory(new StonecuttingCategory());
        registry.addCatalyst(StonecuttingRecipe.class, new ItemStack(Material.STONECUTTER));

        registry.addCategory(new BrewingCategory());
        registry.addCatalyst(PotionMix.class, new ItemStack(Material.BREWING_STAND));

        Bukkit.recipeIterator().forEachRemaining(registry::addRecipe);
    }
}