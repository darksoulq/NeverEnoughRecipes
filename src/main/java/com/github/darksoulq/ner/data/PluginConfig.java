package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.common.config.Config;

public class PluginConfig {
    public final Config config = new Config("ner", "config").schema(1).apply();

    public final Config.Value<Boolean> bookOnJoin = config.value("book.on_join", false);
    public final Config.Value<Boolean> metrics = config.value("metrics", true);
    public final Config.Value<Boolean> enableItemGroups = config.value("groups.enabled", true);

    public PluginConfig() {
        config.save();
    }
}