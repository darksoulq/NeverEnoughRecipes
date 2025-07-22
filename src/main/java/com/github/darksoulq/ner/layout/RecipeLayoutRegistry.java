package com.github.darksoulq.ner.layout;

import java.util.HashMap;
import java.util.Map;

public final class RecipeLayoutRegistry {

    private static final Map<Class<?>, RecipeLayout<?>> REGISTRY = new HashMap<>();

    public static <T> void register(RecipeLayout<T> layout) {
        REGISTRY.put(layout.getRecipeClass(), layout);
    }

    @SuppressWarnings("unchecked")
    public static <T> RecipeLayout<T> getLayout(Class<?> clazz) {
        for (Map.Entry<Class<?>, RecipeLayout<?>> entry : REGISTRY.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return (RecipeLayout<T>) entry.getValue();
            }
        }
        return null;
    }

    public static boolean hasLayout(Class<?> clazz) {
        return getLayout(clazz) != null;
    }
}
