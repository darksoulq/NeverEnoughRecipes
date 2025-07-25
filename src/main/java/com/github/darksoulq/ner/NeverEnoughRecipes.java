package com.github.darksoulq.ner;

import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.config.ConfigManager;
import com.github.darksoulq.abyssallib.server.config.ConfigType;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.ner.data.Events;
import com.github.darksoulq.ner.data.InternalCommands;
import com.github.darksoulq.ner.layout.RecipeLayoutRegistry;
import com.github.darksoulq.ner.layout.impl.*;
import com.github.darksoulq.ner.resources.Config;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.UiItems;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class NeverEnoughRecipes extends JavaPlugin {
    public static final String MODID = "ner";
    public static Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        ConfigManager.register(Config.class, ConfigType.YAML);
        Pack.init(this);
        registerDefaults();
        CommandBus.register(MODID, new InternalCommands());
        EventBus bus = new EventBus(this);
        bus.register(new Events());

        UiItems.ITEMS.apply();
    }

    private void registerDefaults() {
        RecipeLayoutRegistry.register(new ShapedRecipeLayout());
        RecipeLayoutRegistry.register(new ShapelessRecipeLayout());
        RecipeLayoutRegistry.register(new TransmuteRecipeLayout());
        RecipeLayoutRegistry.register(new FurnaceRecipeLayout());
        RecipeLayoutRegistry.register(new BlastingRecipeLayout());
        RecipeLayoutRegistry.register(new SmokingRecipeLayout());
        RecipeLayoutRegistry.register(new CampfireRecipeLayout());
        RecipeLayoutRegistry.register(new SmithingTransformRecipeLayout());
        RecipeLayoutRegistry.register(new BrewingRecipeLayout());
        RecipeLayoutRegistry.register(new StonecuttingRecipeLayout());
    }
}
