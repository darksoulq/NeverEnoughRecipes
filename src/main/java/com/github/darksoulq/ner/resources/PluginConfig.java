package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.common.config.Config;

public class PluginConfig {
    public Config cfg = new Config("ner", "config");
    public Book book;

    public PluginConfig() {
        book = new Book(cfg);
    }

    public static class Book {
        public Config.Value<Boolean> onJoin;

        public Book(Config cfg) {
            onJoin = cfg.value("book.on_join", false);
        }
    }
}
