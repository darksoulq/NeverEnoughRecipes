package com.github.darksoulq.ner.data;

import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import com.github.darksoulq.ner.gui.MainMenu;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public class InternalCommands {
    @Command(name = "ner")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.requires(ctx -> ctx.getSender().hasPermission(NeverEnoughRecipes.config().perms.openGui.get()))
                .executes(ctx -> {
            if (!(ctx.getSource().getSender() instanceof Player p)) return Command.SUCCESS;
            GuiManager.open(p, MainMenu.create());
            return Command.SUCCESS;
        }
        );
    }
}
