package com.github.darksoulq.ner;

import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.ner.data.Events;
import com.github.darksoulq.ner.data.InternalCommands;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.layout.RecipeLayoutRegistry;
import com.github.darksoulq.ner.layout.impl.*;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.PlayerPref;
import com.github.darksoulq.ner.resources.PluginConfig;
import com.github.darksoulq.ner.resources.UiItems;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class NeverEnoughRecipes extends JavaPlugin {
    public static NeverEnoughRecipes INSTANCE;
    public static final String MODID = "ner";
    public static Logger LOGGER;
    private static PluginConfig CONFIG;
    private static PlayerPref PREFS;

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();
        PREFS = new PlayerPref();
        CONFIG = new PluginConfig();
        CONFIG.cfg.save();
        PREFS.cfg.save();
        Pack.init(this);
        registerDefaults();
        CommandBus.register(MODID, new InternalCommands());
        EventBus bus = new EventBus(this);
        bus.register(new Events());

        UiItems.ITEMS.apply();
    }

    public static PluginConfig config() {
        return CONFIG;
    }
    public static PlayerPref getPrefs() {
        return PREFS;
    }
    public static void updatePrefs(Consumer<PlayerPref> updater) {
        updater.accept(PREFS);
        PREFS.cfg.save();
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
        RecipeLayoutRegistry.register(new StonecuttingRecipeLayout());
    }
}
