package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.common.config.Config;

public class PluginConfig {
    public Config cfg = new Config("ner", "config");
    public Book book;
    public Permissions perms;

    public PluginConfig() {
        book = new Book(cfg);
        perms = new Permissions(cfg);
    }

    public static class Permissions {
        public Config.Value<String> openGui;

        public Permissions(Config cfg) {
            openGui = cfg.value("permissions.open_gui", "ner.gui.open");
        }
    }

    public static class Book {
        public Config.Value<Boolean> onJoin;

        public Book(Config cfg) {
            onJoin = cfg.value("book.on_join", false);
        }
    }
}
