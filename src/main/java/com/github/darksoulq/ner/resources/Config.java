package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.server.config.annotation.Comment;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigFile;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigProperty;
import com.github.darksoulq.abyssallib.server.config.annotation.Nest;
import com.github.darksoulq.ner.data.InputType;

@ConfigFile(pluginId = "ner")
public class Config {
    @ConfigProperty(name = "open_for_item")
    @Comment(comments = {"which keybind to use for opening an items recipe", "Possible Values: NONE, SHIFT_Q, and SHIFT_F"})
    public static InputType openWith = InputType.NONE;

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
