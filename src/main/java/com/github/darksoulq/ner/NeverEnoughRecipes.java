package com.github.darksoulq.ner;

import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.ner.data.Events;
import com.github.darksoulq.ner.data.InternalCommands;
import com.github.darksoulq.ner.data.PluginConfig;
import com.github.darksoulq.ner.plugin.InternalRegistration;
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.NerRegistrationEvent;
import com.github.darksoulq.ner.plugin.VanillaNerPlugin;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.PluginPermissions;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class NeverEnoughRecipes extends JavaPlugin {
    public static NeverEnoughRecipes INSTANCE;
    public static PluginConfig CONFIG;
    public static final String PLUGIN_ID = "ner";
    private static final List<NerPlugin> PLUGINS = new ArrayList<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        CONFIG = new PluginConfig();

        Pack.init(this);
        UiItems.ITEMS.apply();
        PluginPermissions.NAMESPACE.apply();
        CommandBus.register(PLUGIN_ID, new InternalCommands());

        EventBus bus = new EventBus(this);
        bus.register(new Events());

        registerPlugin(new VanillaNerPlugin());

        TaskUtil.delayedTask(this, 1, NeverEnoughRecipes::reloadRegistries);
    }

    public static void registerPlugin(NerPlugin plugin) {
        PLUGINS.add(plugin);
    }

    public static void reloadRegistries() {
        IngredientManager.clear();
        RecipeManager.clear();

        InternalRegistration registration = new InternalRegistration();
        for (NerPlugin plugin : PLUGINS) {
            plugin.register(registration);
        }

        EventBus.post(new NerRegistrationEvent(registration));
        RecipeManager.compile();
    }
}