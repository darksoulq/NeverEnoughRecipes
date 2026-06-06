package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.gui.ControlsMenu;
import com.github.darksoulq.ner.gui.InventoryBackupManager;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.resources.PluginPermissions;
import org.bukkit.entity.Player;

public class InternalCommands extends BaseCommand {

    public InternalCommands() {
        super("ner");

        setRequirement(DefaultConditions.hasPerm(PluginPermissions.OPEN_GUI));

        setDefaultExecutor(ctx -> {
            if (ctx.getSource().getSender() instanceof Player p) {
                InventoryBackupManager.save(p);
                GuiManager.open(p, MainMenu.create(p));
            }
            return CommandResult.success();
        });

        BaseCommand configCommand = new BaseCommand("config") {};
        configCommand.setDefaultExecutor(ctx -> {
            if (ctx.getSource().getSender() instanceof Player p) {
                InventoryBackupManager.save(p);
                GuiManager.open(p, ControlsMenu.create(p));
            }
            return CommandResult.success();
        });
        addSubcommand(configCommand);

        BaseCommand reloadCommand = new BaseCommand("reload") {};
        reloadCommand.setRequirement(DefaultConditions.hasPerm(PluginPermissions.RELOAD));
        reloadCommand.setDefaultExecutor(ctx -> {
            NeverEnoughRecipes.reloadRegistries();
            ctx.getSource().getSender().sendRichMessage("<green>NER registries reloaded successfully.");
            return CommandResult.success();
        });
        addSubcommand(reloadCommand);
    }
}