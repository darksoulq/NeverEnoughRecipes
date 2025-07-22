package com.github.darksoulq.ner.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TextUtil {
    public static MiniMessage MM = MiniMessage.miniMessage();

    public static Component parse(String text, TagResolver... resolvers) {
        return MM.deserialize(text, resolvers);
    }
    public static Component parse(String text) {
        return MM.deserialize(text);
    }
    public static String unparse(Component component) {
        return MM.serialize(component);
    }
}
