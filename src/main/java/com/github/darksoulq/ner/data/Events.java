package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.gui.InventoryBackupManager;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.resources.PluginPermissions;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events {
    @SubscribeEvent
    public void onPlayerJoin(PlayerJoinEvent event) {
        UserManager.get(event.getPlayer().getUniqueId());
        if (!NeverEnoughRecipes.CONFIG.bookOnJoin.get()) return;
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) return;
        player.getInventory().addItem(UiItems.DEFAULT_BOOK.getStack().clone());
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        Item item = Item.resolve(event.getItem());
        if (item == null || !item.equals(UiItems.DEFAULT_BOOK)) return;
        if (!PluginPermissions.OPEN_GUI.has(event.getPlayer())) return;
        InventoryBackupManager.save(event.getPlayer());
        GuiManager.open(event.getPlayer(), MainMenu.create(event.getPlayer()));
    }
}