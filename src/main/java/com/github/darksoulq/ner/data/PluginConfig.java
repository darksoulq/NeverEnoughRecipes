package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.common.config.Config;

public class PluginConfig {
    public final Config config = new Config("ner", "config").schema(1).apply();

    public final Config.Value<Boolean> bookOnJoin = config.value("book.on_join", false);
    public final Config.Value<Boolean> metrics = config.value("metrics", true);
    public final Config.Value<Boolean> enableItemGroups = config.value("groups.enabled", true);

    public final Config.Value<Boolean> enableCrafting = config.value("recipes.crafting", true);
    public final Config.Value<Boolean> enableSmelting = config.value("recipes.smelting", true);
    public final Config.Value<Boolean> enableBlasting = config.value("recipes.blasting", true);
    public final Config.Value<Boolean> enableSmoking = config.value("recipes.smoking", true);
    public final Config.Value<Boolean> enableCampfire = config.value("recipes.campfire", true);
    public final Config.Value<Boolean> enableStonecutting = config.value("recipes.stonecutting", true);
    public final Config.Value<Boolean> enableSmithing = config.value("recipes.smithing", true);

    //? if >=26.3 {
    /*public final Config.Value<Boolean> enableVillagerTrades = config.value("recipes.vanilla.villager_trades", true);
    public final Config.Value<Boolean> enableWanderingTrader = config.value("recipes.vanilla.wandering_trader", true);
    public final Config.Value<Boolean> enableBrewing = config.value("recipes.brewing", true);
    *///?} else {
    public final Config.Value<Boolean> enableVillagerTrades = config.value("recipes.villager_trades", true);
    public final Config.Value<Boolean> enableWanderingTrader = config.value("recipes.wandering_trader", true);
    //?}

    public final Config.Value<Boolean> enableCartographerTrades = config.value("recipes.villager_cartographer", true);

    public PluginConfig() {
        config.save();
    }
}