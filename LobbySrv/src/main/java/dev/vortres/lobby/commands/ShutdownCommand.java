package dev.vortres.lobby.commands;

import dev.vortres.lobby.util.MessageHelper;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A simple shutdown command.
 */
public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        super("shutdown", "stop");
        addSyntax(this::execute);
    }

    private void execute(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {
        // Save Instance
        SaveCommand.saveInstance();

        // Kick players
        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                    for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers())
                        player.kick(Component.text("Server is shutting down", MessageHelper.RED_COLOR));
                }
        );

        // Stop server
        CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(MinecraftServer::stopCleanly);
    }
}
