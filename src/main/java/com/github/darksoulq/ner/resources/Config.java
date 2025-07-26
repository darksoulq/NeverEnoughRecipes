package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.config.annotation.Comment;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigFile;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigProperty;
import com.github.darksoulq.abyssallib.server.config.annotation.Nest;

@ConfigFile(pluginId = "ner")
public class Config {

    @Nest(name = "permissions")
    public static class Permissions {
        @ConfigProperty(name = "open_gui")
        public static String openGui = "ner.command.gui";
    }

    @Nest(name = "book")
    public static class Book {
        @ConfigProperty(name = "enable")
        @Comment(comments = "Whether the book item should be enabled")
        public static boolean enable = false;

        @ConfigProperty(name = "on_join")
        @Comment(comments = "Whether the book item should be given on first join")
        public static boolean onJoin = true;
    }
}
