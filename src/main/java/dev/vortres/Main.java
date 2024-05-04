package dev.vortres;

import dev.vortres.block.TestBlockHandler;
import dev.vortres.block.placement.DripstonePlacementRule;
import dev.vortres.commands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extras.lan.OpenToLAN;
import net.minestom.server.extras.lan.OpenToLANConfig;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;

import static dev.vortres.config.ConfigHandler.CONFIG;

public class Main {

    public static void main(String[] args) {
        // Server Init
        MinecraftServer.setCompressionThreshold(128);
        MinecraftServer minecraftServer = MinecraftServer.init();

        MinecraftServer.getBenchmarkManager().enable(Duration.of(10, TimeUnit.SECOND));
        MinecraftServer.setBrandName("Vortres");
        if (CONFIG.prometheus().enabled()) Metrics.init();

        // Events
        BlockManager blockManager = MinecraftServer.getBlockManager();
        blockManager.registerBlockPlacementRule(new DripstonePlacementRule());
        blockManager.registerHandler(TestBlockHandler.INSTANCE.getNamespaceId(), () -> TestBlockHandler.INSTANCE);

        // Command Manager
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new HealthCommand());
        commandManager.register(new ShutdownCommand());
        commandManager.register(new TeleportCommand());
        commandManager.register(new PlayersCommand());
        commandManager.register(new FindCommand());
        commandManager.register(new GiveCommand());
        commandManager.register(new SaveCommand());
        commandManager.register(new GamemodeCommand());
        commandManager.register(new SpawnCommand());
        commandManager.register(new SetBlockCommand());
        commandManager.register(new TestMessageHelper());
        commandManager.register(new SummonCommand());
        commandManager.register(new WorldCommand());

        commandManager.setUnknownCommandCallback((sender, command) -> sender.sendMessage(Component.text("> Unknown command", TextColor.color(255, 50, 50))));

        // Initialization
        Initialization.init();

        // Start Server
        minecraftServer.start("0.0.0.0", 25565);
        OpenToLAN.open(new OpenToLANConfig().eventCallDelay(Duration.of(1, TimeUnit.DAY)));
        Runtime.getRuntime().addShutdownHook(new Thread(MinecraftServer::stopCleanly));
    }
}