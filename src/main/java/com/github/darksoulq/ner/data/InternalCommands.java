package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.command.AbyssalCommand;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiManager;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.resources.Config;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public class InternalCommands {

    @AbyssalCommand(name = "ner")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.requires(ctx -> ctx.getSender().hasPermission(Config.Permissions.openGui))
                .executes(ctx -> {
            if (!(ctx.getSource().getSender() instanceof Player p)) return Command.SINGLE_SUCCESS;
            GuiManager.open(p, MainMenu.create());
            return Command.SINGLE_SUCCESS;
        }
        );
    }
}
