package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.gui.ControlsMenu;
import com.github.darksoulq.ner.gui.InventoryBackupManager;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.resources.PluginPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class InternalCommands {
    @Command(name = "ner")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.requires(DefaultConditions.hasPerm(PluginPermissions.OPEN_GUI))
            .executes(ctx -> {
                if (ctx.getSource().getSender() instanceof Player p) {
                    InventoryBackupManager.save(p);
                    GuiManager.open(p, MainMenu.create(p));
                }
                return Command.SUCCESS;
            })
            .then(Commands.literal("config")
                .executes(ctx -> {
                    if (ctx.getSource().getSender() instanceof Player p) {
                        InventoryBackupManager.save(p);
                        GuiManager.open(p, ControlsMenu.create(p));
                    }
                    return Command.SUCCESS;
                })
            )
            .then(Commands.literal("reload")
                .requires(DefaultConditions.hasPerm(PluginPermissions.RELOAD))
                .executes(ctx -> {
                    NeverEnoughRecipes.reloadRegistries();
                    ctx.getSource().getSender().sendRichMessage("<green>NER registries reloaded successfully.");
                    return Command.SUCCESS;
                })
            );
    }
}