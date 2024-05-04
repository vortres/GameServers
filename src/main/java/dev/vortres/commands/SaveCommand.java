package me.vortres.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A simple instance save command.
 */
public class SaveCommand extends Command {

    public SaveCommand() {
        super("save");
        addSyntax(this::execute);
    }

    private void execute(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {
        commandSender.sendMessage(Component.text("» ", TextColor.color(0x858585), TextDecoration.BOLD)
                .append(Component.text("Saving instance...", TextColor.color(0x9595FF))));
        saveInstance();
        commandSender.sendMessage(Component.text("» ", TextColor.color(0x858585), TextDecoration.BOLD)
                .append(Component.text("Instance saved!", TextColor.color(0x9595FF))));
    }

    static void saveInstance() {
        for(var instance : MinecraftServer.getInstanceManager().getInstances()) {
            CompletableFuture<Void> instanceSave = instance.saveChunksToStorage().thenCompose(v -> instance.saveChunksToStorage());
            try {
                instanceSave.get();
            } catch (InterruptedException | ExecutionException e) {
                MinecraftServer.getExceptionManager().handleException(e);
            }
        }
    }
}
