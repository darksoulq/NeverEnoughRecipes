package com.github.darksoulq.ner;

import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.scheduler.Scheduler;
import com.github.darksoulq.abyssallib.server.util.UpdateChecker;
import com.github.darksoulq.ner.data.Events;
import com.github.darksoulq.ner.data.InternalCommands;
import com.github.darksoulq.ner.data.PluginConfig;
import com.github.darksoulq.ner.plugin.InternalRegistration;
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.NerRegistrationEvent;
import com.github.darksoulq.ner.plugin.VanillaNerPlugin;
import com.github.darksoulq.ner.registry.GroupManager;
import com.github.darksoulq.ner.registry.IngredientManager;
import com.github.darksoulq.ner.registry.RecipeManager;
import com.github.darksoulq.ner.resources.Pack;
import com.github.darksoulq.ner.resources.PluginPermissions;
import com.github.darksoulq.ner.resources.UiItems;
import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class NeverEnoughRecipes extends JavaPlugin {
    public static NeverEnoughRecipes INSTANCE;
    public static PluginConfig CONFIG;
    public static Scheduler SCHEDULER;
    public static final String PLUGIN_ID = "ner";
    private static final List<NerPlugin> PLUGINS = new ArrayList<>();

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    private static final BukkitMetrics.Factory METRICS = BukkitMetrics.factory()
        .token("5c032aeabbd0ba0d0193522502b8f460")
        .errorTracker(ERROR_TRACKER);

    @Override
    public void onEnable() {
        INSTANCE = this;
        CONFIG = new PluginConfig();
        SCHEDULER = new Scheduler(this);

        Pack.init(this);
        UiItems.ITEMS.apply();
        PluginPermissions.NAMESPACE.apply();
        CommandBus.register(PLUGIN_ID, new InternalCommands());

        EventBus bus = new EventBus(this);
        bus.register(new Events());

        registerPlugin(new VanillaNerPlugin());

        SCHEDULER.schedule(NeverEnoughRecipes::reloadRegistries).after(10L, Clock.TICKS).once();

        if (CONFIG.metrics.get()) METRICS.create(this).ready();

        new UpdateChecker(this, "neverenoughrecipes", true).check(result -> {
            getLogger().warning("A new update is available: " + result.version());
            getLogger().warning("Download at: " + result.link());
        });
    }

    public static void registerPlugin(NerPlugin plugin) {
        PLUGINS.add(plugin);
    }

    public static void reloadRegistries() {
        IngredientManager.clear();
        RecipeManager.clear();
        GroupManager.clear();

        InternalRegistration registration = new InternalRegistration();
        for (NerPlugin plugin : PLUGINS) {
            plugin.register(registration);
        }

        EventBus.post(new NerRegistrationEvent(registration));
        IngredientManager.runNamespaceProviders();
        RecipeManager.compile();
    }
}