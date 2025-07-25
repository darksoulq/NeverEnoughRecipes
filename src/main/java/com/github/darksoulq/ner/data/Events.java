package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.gui.RecipeViewer;
import com.github.darksoulq.ner.resources.Config;
import com.github.darksoulq.ner.resources.UiItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.PlayerInventory;

public class Events {
    @SubscribeEvent
    public void onServerLoad(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;
        RecipeManager.loadVanillaRecipes();
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Config.Book.enable) return;
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) return;
        player.getInventory().addItem(UiItems.DEFAULT_BOOK.get().getStack().clone());
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        Item item = Item.from(event.getItem());
        if (item == null) return;
        if (!item.equals(UiItems.DEFAULT_BOOK.get())) return;
        GuiManager.open(event.getPlayer(), MainMenu.create());
    }

    @SubscribeEvent
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (Config.openWith == InputType.NONE) return;
        if (!(event.getClickedInventory() instanceof PlayerInventory)) return;
        if (!RecipeManager.getAllItems().contains(event.getCurrentItem())) return;
        Player player = (Player) event.getWhoClicked();
        if (!player.isSneaking()) return;
        if (Config.openWith == InputType.SHIFT_Q) {
            if (!event.getAction().equals(InventoryAction.DROP_ONE_SLOT)) return;
        } else {
            if (!event.getClick().equals(ClickType.SWAP_OFFHAND)) return;
        }
        event.setCancelled(true);
        GuiManager.open(player, RecipeViewer.create(event.getCurrentItem()));
    }
}
