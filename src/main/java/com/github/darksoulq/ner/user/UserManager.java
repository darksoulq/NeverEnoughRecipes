package com.github.darksoulq.ner.user;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private static final Map<UUID, PlayerSettings> SETTINGS = new ConcurrentHashMap<>();

    public static PlayerSettings get(UUID uuid) {
        return SETTINGS.computeIfAbsent(uuid, PlayerSettings::new);
    }
}