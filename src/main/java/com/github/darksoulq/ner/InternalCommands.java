package com.github.darksoulq.ner;

import com.github.darksoulq.abyssallib.server.command.AbyssalCommand;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiManager;
import com.github.darksoulq.ner.gui.MainMenu;
import com.github.darksoulq.ner.gui.RecipeViewer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InternalCommands {

    @AbyssalCommand(name = "ner")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.executes(ctx -> {
            if (!(ctx.getSource().getSender() instanceof Player p)) return Command.SINGLE_SUCCESS;
            GuiManager.open(p, MainMenu.create());
            return Command.SINGLE_SUCCESS;
        });
    }
}
