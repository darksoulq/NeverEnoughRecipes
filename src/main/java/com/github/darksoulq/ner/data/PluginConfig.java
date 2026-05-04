package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.common.config.Config;

public class PluginConfig {
    public final Config config = new Config("ner", "config");
    public final Config.Value<Boolean> bookOnJoin = config.value("book.on_join", false);

    public PluginConfig() {
        config.save();
    }
}