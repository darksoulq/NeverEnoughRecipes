package com.github.darksoulq.ner;

import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import org.bukkit.event.server.ServerLoadEvent;

public class Events {
    @SubscribeEvent
    public void onServerLoad(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;
        RecipeManager.loadVanillaRecipes();
    }
}
