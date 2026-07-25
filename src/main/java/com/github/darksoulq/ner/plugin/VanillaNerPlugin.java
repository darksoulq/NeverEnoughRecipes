package com.github.darksoulq.ner.plugin;

import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.ner.layout.impl.*;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.util.CraftabilityUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class VanillaNerPlugin implements NerPlugin {

    private static final Map<Material, Integer> CREATIVE_ORDER = new EnumMap<>(Material.class);
    private static boolean creativeOrderInitialized = false;

    private static final Map<Material, String> VANILLA_TAGS = new EnumMap<>(Material.class);
    private static boolean vanillaTagsLoaded = false;

    private static int getCreativeOrder(Material material) {
        if (!creativeOrderInitialized) {
            int order = 0;
            try {
                MinecraftServer server = net.minecraft.server.MinecraftServer.getServer();
                FeatureFlagSet features = server.getWorldData().enabledFeatures();
                HolderLookup.Provider provider = server.registryAccess();
                CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(features, true, provider);

                Iterable<CreativeModeTab> tabs = BuiltInRegistries.CREATIVE_MODE_TAB;
                for (CreativeModeTab tab : tabs) {
                    if (tab.getType() == CreativeModeTab.Type.CATEGORY) {
                        try {
                            tab.buildContents(params);
                            java.util.Collection<net.minecraft.world.item.ItemStack> items = tab.getDisplayItems();
                            for (net.minecraft.world.item.ItemStack nmsItem : items) {
                                if (nmsItem != null && !nmsItem.isEmpty()) {
                                    Material mat = CraftItemStack.asBukkitCopy(nmsItem).getType();
                                    CREATIVE_ORDER.putIfAbsent(mat, order++);
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } catch (Throwable ignored) {}
            creativeOrderInitialized = true;
        }
        return CREATIVE_ORDER.getOrDefault(material, Integer.MAX_VALUE);
    }

    private static void loadVanillaTags() {
        if (vanillaTagsLoaded) return;
        for (org.bukkit.Tag<Material> tag : Bukkit.getTags(org.bukkit.Tag.REGISTRY_ITEMS, Material.class)) {
            String tagNamespace = tag.getKey().getNamespace().toLowerCase(Locale.ROOT);
            String tagKey = tag.getKey().getKey().toLowerCase(Locale.ROOT);
            String fullTag = tagNamespace + ":" + tagKey;
            for (Material mat : tag.getValues()) {
                VANILLA_TAGS.merge(mat, fullTag, (a, b) -> a + ";" + b);
            }
        }
        vanillaTagsLoaded = true;
    }

    @Override
    public void register(Registration registry) {
        loadVanillaTags();

        registry.addDeduplicator(item -> {
            if (item.getType() == Material.SUSPICIOUS_STEW) {
                return new ItemStack(Material.SUSPICIOUS_STEW);
            }
            return item;
        });

        registry.setNamespaceComparator("minecraft", (a, b) -> {
            int orderA = a.fold(
                item -> getCreativeOrder(item.getType()),
                group -> group.items().isEmpty() ? Integer.MAX_VALUE : getCreativeOrder(group.items().getFirst().getType())
            );
            int orderB = b.fold(
                item -> getCreativeOrder(item.getType()),
                group -> group.items().isEmpty() ? Integer.MAX_VALUE : getCreativeOrder(group.items().getFirst().getType())
            );
            return Integer.compare(orderA, orderB);
        });

        registry.addFilter("@", (term, item) -> {
            String ns = IngredientManager.getNamespace(item);
            return ns.toLowerCase(Locale.ROOT).contains(term);
        });

        registry.addFilter(":", (term, item) -> {
            if (!item.hasData(DataComponentTypes.LORE)) return false;
            ItemLore lore = item.getData(DataComponentTypes.LORE);
            if (lore == null || lore.lines().isEmpty()) return false;
            for (Component line : lore.lines()) {
                if (PlainTextComponentSerializer.plainText().serialize(line).toLowerCase(Locale.ROOT).contains(term)) {
                    return true;
                }
            }
            return false;
        });

        registry.addFilter("#", (term, item) -> {
            String vTags = VANILLA_TAGS.get(item.getType());
            if (vTags != null && vTags.contains(term)) return true;

            for (Map.Entry<String, Tag<?, ?>> entry : Registries.TAGS.getAll().entrySet()) {
                if (entry.getValue() instanceof ItemTag itemTag) {
                    if (entry.getKey().toLowerCase(Locale.ROOT).contains(term) && itemTag.contains(item)) {
                        return true;
                    }
                }
            }
            return false;
        });

        registry.addFilter("$", (term, item) -> !RecipeManager.getRecipes(item).stream()
            .filter(recipe -> recipe.provider() != null && !recipe.provider().isEmpty())
            .filter(recipe -> {
                Component nameComp = recipe.provider().hasData(DataComponentTypes.CUSTOM_NAME) ? recipe.provider().getData(DataComponentTypes.CUSTOM_NAME) : (recipe.provider().hasData(DataComponentTypes.ITEM_NAME) ? recipe.provider().getData(DataComponentTypes.ITEM_NAME) : Component.text(recipe.provider().getType().name()));
                return PlainTextComponentSerializer.plainText().serialize(nameComp).toLowerCase(Locale.ROOT).contains(term);
            })
            .toList().isEmpty() ||
            !RecipeManager.getUses(item).stream()
                .filter(recipe -> recipe.provider() != null && !recipe.provider().isEmpty())
                .filter(recipe -> {
                    Component nameComp = recipe.provider().hasData(DataComponentTypes.CUSTOM_NAME) ? recipe.provider().getData(DataComponentTypes.CUSTOM_NAME) : (recipe.provider().hasData(DataComponentTypes.ITEM_NAME) ? recipe.provider().getData(DataComponentTypes.ITEM_NAME) : Component.text(recipe.provider().getType().name()));
                    return PlainTextComponentSerializer.plainText().serialize(nameComp).toLowerCase(Locale.ROOT).contains(term);
                })
                .toList().isEmpty());

        List<ItemStack> beds = new ArrayList<>();
        List<ItemStack> wool = new ArrayList<>();
        List<ItemStack> carpets = new ArrayList<>();
        List<ItemStack> terracotta = new ArrayList<>();
        List<ItemStack> glazedTerracotta = new ArrayList<>();
        List<ItemStack> concrete = new ArrayList<>();
        List<ItemStack> concretePowder = new ArrayList<>();
        List<ItemStack> stainedGlass = new ArrayList<>();
        List<ItemStack> stainedGlassPanes = new ArrayList<>();
        List<ItemStack> shulkerBoxes = new ArrayList<>();
        List<ItemStack> banners = new ArrayList<>();
        List<ItemStack> candles = new ArrayList<>();

        for (Material mat : Material.values()) {
            if (!mat.isItem() || mat.isAir() || mat.isLegacy()) continue;
            String name = mat.name();
            if (name.endsWith("_BED")) beds.add(new ItemStack(mat));
            else if (name.endsWith("_WOOL")) wool.add(new ItemStack(mat));
            else if (name.endsWith("_CARPET")) carpets.add(new ItemStack(mat));
            else if (name.endsWith("_GLAZED_TERRACOTTA")) glazedTerracotta.add(new ItemStack(mat));
            else if (name.endsWith("_TERRACOTTA") && !name.equals("TERRACOTTA")) terracotta.add(new ItemStack(mat));
            else if (name.endsWith("_CONCRETE")) concrete.add(new ItemStack(mat));
            else if (name.endsWith("_CONCRETE_POWDER")) concretePowder.add(new ItemStack(mat));
            else if (name.endsWith("_STAINED_GLASS")) stainedGlass.add(new ItemStack(mat));
            else if (name.endsWith("_STAINED_GLASS_PANE")) stainedGlassPanes.add(new ItemStack(mat));
            else if (name.endsWith("_SHULKER_BOX")) shulkerBoxes.add(new ItemStack(mat));
            else if (name.endsWith("_BANNER") && !name.contains("PATTERN") && !name.contains("WALL")) banners.add(new ItemStack(mat));
            else if (name.endsWith("_CANDLE")) candles.add(new ItemStack(mat));
        }

        Comparator<ItemStack> creativeSorter = Comparator.comparingInt(a -> getCreativeOrder(a.getType()));
        beds.sort(creativeSorter);
        wool.sort(creativeSorter);
        carpets.sort(creativeSorter);
        terracotta.sort(creativeSorter);
        glazedTerracotta.sort(creativeSorter);
        concrete.sort(creativeSorter);
        concretePowder.sort(creativeSorter);
        stainedGlass.sort(creativeSorter);
        stainedGlassPanes.sort(creativeSorter);
        shulkerBoxes.sort(creativeSorter);
        banners.sort(creativeSorter);
        candles.sort(creativeSorter);

        registry.addItemGroup("beds", Component.text("Beds"), beds, true);
        registry.addItemGroup("wool", Component.text("Wool"), wool, true);
        registry.addItemGroup("carpets", Component.text("Carpets"), carpets, true);
        registry.addItemGroup("terracotta", Component.text("Terracotta"), terracotta, true);
        registry.addItemGroup("glazed_terracotta", Component.text("Glazed Terracotta"), glazedTerracotta, true);
        registry.addItemGroup("concrete", Component.text("Concrete"), concrete, true);
        registry.addItemGroup("concrete_powder", Component.text("Concrete Powder"), concretePowder, true);
        registry.addItemGroup("stained_glass", Component.text("Stained Glass"), stainedGlass, true);
        registry.addItemGroup("stained_glass_panes", Component.text("Stained Glass Panes"), stainedGlassPanes, true);
        registry.addItemGroup("shulker_boxes", Component.text("Shulker Boxes"), shulkerBoxes, true);
        registry.addItemGroup("banners", Component.text("Banners"), banners, true);
        registry.addItemGroup("candles", Component.text("Candles"), candles, true);

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

        registry.addCraftabilityChecker(ShapedRecipe.class, (player, recipe) -> {
            List<RecipeChoice> choices = new ArrayList<>();
            for (String row : recipe.getShape()) {
                for (char c : row.toCharArray()) {
                    RecipeChoice choice = recipe.getChoiceMap().get(c);
                    if (choice != null) choices.add(choice);
                }
            }
            return CraftabilityUtil.hasIngredients(player, choices);
        });

        registry.addCraftabilityChecker(ShapelessRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, recipe.getChoiceList()));

        registry.addCraftabilityChecker(TransmuteRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInput(), recipe.getMaterial())));

        registry.addCraftabilityChecker(FurnaceRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInputChoice())));

        registry.addCraftabilityChecker(BlastingRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInputChoice())));

        registry.addCraftabilityChecker(SmokingRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInputChoice())));

        registry.addCraftabilityChecker(CampfireRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInputChoice())));

        registry.addCraftabilityChecker(SmithingTransformRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getTemplate(), recipe.getBase(), recipe.getAddition())));

        registry.addCraftabilityChecker(StonecuttingRecipe.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInputChoice())));

        registry.addCraftabilityChecker(PotionMix.class, (player, recipe) ->
            CraftabilityUtil.hasIngredients(player, List.of(recipe.getInput(), recipe.getIngredient())));

        Bukkit.recipeIterator().forEachRemaining(registry::addRecipe);
    }
}