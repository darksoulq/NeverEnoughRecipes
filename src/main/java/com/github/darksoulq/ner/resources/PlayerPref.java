package com.github.darksoulq.ner.resources;

import com.github.darksoulq.abyssallib.common.config.Config;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerPref {
    public Config cfg = new Config("ner", "player_prefs", "data");
    public Config.Value<Map<String, List<ItemStack>>> recents;
    public Config.Value<Map<String, List<ItemStack>>> favourites;

    public PlayerPref() {
        recents = cfg.value("recents", new HashMap<>());
        favourites = cfg.value("favourites", new HashMap<>());
    }
}
