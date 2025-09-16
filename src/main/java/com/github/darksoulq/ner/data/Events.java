package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.gui.SearchMenu;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.util.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;

public class Events {
    @SubscribeEvent
    public void onServerLoad(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;
        RecipeManager.loadVanillaRecipes();
        TaskUtil.delayedTask(5, MainMenu::init);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!NeverEnoughRecipes.config().book.onJoin.get()) return;
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) return;
        player.getInventory().addItem(UiItems.DEFAULT_BOOK.get().getStack().clone());
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        Item item = Item.resolve(event.getItem());
        if (item == null) return;
        if (!item.equals(UiItems.DEFAULT_BOOK.get())) return;
        if (!event.getPlayer().hasPermission(NeverEnoughRecipes.config().perms.openGui.get())) return;
        GuiManager.open(event.getPlayer(), SearchMenu.create());
    }
}
