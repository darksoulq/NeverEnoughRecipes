package com.github.darksoulq.ner.gui;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.ner.model.ControlAction;
import com.github.darksoulq.ner.resources.UiItems;
import com.github.darksoulq.ner.user.PlayerSettings;
import com.github.darksoulq.ner.user.UserManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.Arrays;

public class ControlsMenu {
    private static final ClickType[] BINDS = { ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT, ClickType.MIDDLE, ClickType.DROP };

    public static Gui create(Player player) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());

        return Gui.builder(MenuType.GENERIC_9X4, TextUtil.parse("<white>Controls Configuration"))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .set(SlotPosition.top(11), buildButton(ControlAction.VIEW_RECIPE, player))
            .set(SlotPosition.top(13), buildButton(ControlAction.VIEW_USES, player))
            .set(SlotPosition.top(15), buildButton(ControlAction.TOGGLE_BOOKMARK, player))
            .set(SlotPosition.top(20), buildReturnFromSearchToggle(player))
            .set(SlotPosition.top(21), buildReturnFromConfigToggle(player))
            .set(SlotPosition.top(35), new GuiButton(UiItems.CLOSE.getStack(), ctx -> {
                InventoryBackupManager.transition(ctx.view());
                if (settings.returnToMenuOnConfigClose.get()) {
                    AbyssalLib.SCHEDULER.schedule(() -> GuiManager.open(player, MainMenu.create(player))).after(1L, Clock.TICKS).entity(player).once();
                }
            }))
            .onOpen(InventoryBackupManager::setup)
            .onClose((view) -> {
                InventoryBackupManager.restore(view);
                if (settings.returnToMenuOnConfigClose.get()) {
                    AbyssalLib.SCHEDULER.schedule(() -> GuiManager.open(player, MainMenu.create(player))).after(1L, Clock.TICKS).entity(player).once();
                }
            }).build();
    }

    private static GuiButton buildButton(ControlAction action, Player player) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        ClickType current = settings.getBind(action);
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        item.editMeta(m -> {
            m.displayName(TextUtil.parse("<!italic><green>" + action.name()));
            m.lore(Arrays.asList(
                TextUtil.parse("<!italic><gray>Current: <yellow>" + current.name()),
                TextUtil.parse("<!italic><dark_gray>Click to cycle bind")
            ));
        });
        return new GuiButton(item, ctx -> {
            ClickType next = cycle(current);
            settings.setBind(action, next);
            InventoryBackupManager.transition(ctx.view());
            GuiManager.open(player, create(player));
        });
    }

    private static GuiButton buildReturnFromSearchToggle(Player player) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        boolean enabled = settings.returnToMenuOnSearchClose.get();
        ItemStack item = new ItemStack(Material.COMPASS);
        item.editMeta(m -> {
            m.displayName(TextUtil.parse("<!italic><gold>Return to Main Menu"));
            m.lore(Arrays.asList(
                TextUtil.parse("<!italic><gray>Action: <yellow>On Search Close"),
                TextUtil.parse("<!italic><gray>Status: " + (enabled ? "<green>Enabled</green>" : "<red>Disabled</red>")),
                TextUtil.parse("<!italic><dark_gray>Click to toggle")
            ));
        });
        return new GuiButton(item, ctx -> {
            settings.returnToMenuOnSearchClose.set(!enabled);
            settings.config.save();
            InventoryBackupManager.transition(ctx.view());
            GuiManager.open(player, create(player));
        });
    }

    private static GuiButton buildReturnFromConfigToggle(Player player) {
        PlayerSettings settings = UserManager.get(player.getUniqueId());
        boolean enabled = settings.returnToMenuOnConfigClose.get();
        ItemStack item = new ItemStack(Material.COMPASS);
        item.editMeta(m -> {
            m.displayName(TextUtil.parse("<!italic><gold>Return to Main Menu"));
            m.lore(Arrays.asList(
                TextUtil.parse("<!italic><gray>Action: <yellow>On Config Close"),
                TextUtil.parse("<!italic><gray>Status: " + (enabled ? "<green>Enabled</green>" : "<red>Disabled</red>")),
                TextUtil.parse("<!italic><dark_gray>Click to toggle")
            ));
        });
        return new GuiButton(item, ctx -> {
            settings.returnToMenuOnConfigClose.set(!enabled);
            settings.config.save();
            InventoryBackupManager.transition(ctx.view());
            GuiManager.open(player, create(player));
        });
    }

    private static ClickType cycle(ClickType current) {
        for (int i = 0; i < BINDS.length; i++) {
            if (BINDS[i] == current) return BINDS[(i + 1) % BINDS.length];
        }
        return BINDS[0];
    }
}