package me.vortres;

import me.vortres.instance.LobbyInstance;
import me.vortres.util.MessageHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.notifications.Notification;
import net.minestom.server.advancements.notifications.NotificationCenter;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.identity.NamedAndIdentified;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static net.minestom.server.MinecraftServer.LOGGER;

public class Initialization {
    private static final EventNode<Event> NODE = EventNode.all("node")
            .addListener(AsyncPlayerConfigurationEvent.class, event -> {
                final Player player = event.getPlayer();

                // Check if there is any instance if not kick the player
                if ( MinecraftServer.getInstanceManager().getInstances().isEmpty() ) {
                    player.kick(Component.text("No instances found", MessageHelper.RED_COLOR));
                    return;
                }
                event.setSpawningInstance(LobbyInstance.INSTANCE);
                player.setRespawnPoint(new Pos(0.5, 18, 0.5));

                // Permission
                player.setPermissionLevel(4);
            }).addListener(PlayerSpawnEvent.class, event -> {
                final Player player = event.getPlayer();

                ItemStack itemStack = ItemStack.builder(Material.STONE)
                        .amount(64)
                        .build();
                player.getInventory().addItemStack(itemStack);

                if (event.isFirstSpawn()) {
                    Notification notification = new Notification(
                            Component.text("Welcome to the server!", MessageHelper.BLUE_COLOR),
                            FrameType.TASK,
                            Material.IRON_SWORD
                    );
                    NotificationCenter.send(notification, event.getPlayer());

                    // Join message
                    Audiences.all().sendMessage(Component.text("[", MessageHelper.GRAY_COLOR)
                            .append(Component.text("+", MessageHelper.BLUE_COLOR))
                            .append(Component.text("] ", MessageHelper.GRAY_COLOR))
                            .append(Component.text(player.getUsername(), MessageHelper.BLUE_ISH_COLOR))
                            .append(Component.text(" joined the server", MessageHelper.GRAY_COLOR))
                    );
                }
            }).addListener(PlayerDisconnectEvent.class, event -> {
                final Player player = event.getPlayer();

                // Leave message
                Audiences.all().sendMessage(Component.text("[", MessageHelper.GRAY_COLOR)
                        .append(Component.text("-", MessageHelper.RED_ISH_COLOR))
                        .append(Component.text("] ", MessageHelper.GRAY_COLOR))
                        .append(Component.text(player.getUsername(), MessageHelper.RED_COLOR))
                        .append(Component.text(" left the server", MessageHelper.GRAY_COLOR))
                );
            }).addListener(PlayerBlockInteractEvent.class, event -> {
                var block = event.getBlock();
                var rawOpenProp = block.getProperty("open");
                if (rawOpenProp == null) return;

                block = block.withProperty("open", String.valueOf(!Boolean.parseBoolean(rawOpenProp)));
                event.getInstance().setBlock(event.getBlockPosition(), block);
            }).addListener(PickupItemEvent.class, event -> {
                final Entity entity = event.getLivingEntity();

                if (entity instanceof Player) {
                    // Cancel event if player does not have enough inventory space
                    final ItemStack itemStack = event.getItemEntity().getItemStack();
                    event.setCancelled(!((Player) entity).getInventory().addItemStack(itemStack));
                }
            }).addListener(ItemDropEvent.class, event -> {
                final Player player = event.getPlayer();
                ItemStack droppedItem = event.getItemStack();

                Pos playerPos = player.getPosition();
                ItemEntity itemEntity = new ItemEntity(droppedItem);
                itemEntity.setPickupDelay(Duration.of(500, TimeUnit.MILLISECOND));
                itemEntity.setInstance(player.getInstance(), playerPos.withY(y -> y + 1.5));
                Vec velocity = playerPos.direction().mul(6);
                itemEntity.setVelocity(velocity);
            });

    // This method is called from Main.java
    public static void init() {
        MinecraftServer.getInstanceManager().registerInstance(LobbyInstance.INSTANCE);

        var eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addChild(NODE);

        // Monitor
        AtomicReference<TickMonitor> lastTick = new AtomicReference<>();
        eventHandler.addListener(ServerTickMonitorEvent.class, event -> {
            final TickMonitor monitor = event.getTickMonitor();
            Metrics.TICK_TIME.observe(monitor.getTickTime());
            Metrics.ACQUISITION_TIME.observe(monitor.getAcquisitionTime());
            lastTick.set(monitor);
        });
        MinecraftServer.getExceptionManager().setExceptionHandler(e -> {
            LOGGER.error("Global exception handler", e);
            Metrics.EXCEPTIONS.labels(e.getClass().getSimpleName()).inc();
        });

        // Playerlist in server list
        eventHandler.addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            if (event.getConnection() != null) {
                responseData.addEntry(NamedAndIdentified.named(Component.text("Hello! this is ZenZoya's Testing server.", MessageHelper.GRAY_COLOR)));
            }

            responseData.setDescription(Component.text("Minestom Server", MessageHelper.BLUE_COLOR)
                    .append(Component.text(" - ", MessageHelper.GRAY_COLOR))
                    .append(Component.text("Testing server", MessageHelper.BLUE_ISH_COLOR))
            );
        });

        // Tablist header and footer
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
            if (players.isEmpty()) return;
            final Runtime runtime = Runtime.getRuntime();
            final TickMonitor tickMonitor = lastTick.get();
            final long ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

            final Component header = Component.newline()
                    .append(Component.text("Vortres Network", MessageHelper.BLUE_COLOR))
                    .append(Component.newline())
                    .append(Component.text("Players: ", MessageHelper.GRAY_COLOR)).append(Component.text(players.size(), MessageHelper.BLUE_ISH_COLOR))
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(Component.text("RAM USAGE: ", MessageHelper.GRAY_COLOR).append(Component.text(ramUsage + "MB", MessageHelper.BLUE_ISH_COLOR))
                    .append(Component.newline())
                    .append(Component.text("TICK TIME: ", MessageHelper.GRAY_COLOR).append(Component.text(MathUtils.round(tickMonitor.getTickTime(), 2) + "ms", MessageHelper.BLUE_ISH_COLOR))))
                    .append(Component.newline());

            final Component footer = Component.newline()
                    .append(Component.text("Project: minestom.net", TextColor.color(0x8C8C8C))
                            .append(Component.newline()));

            Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
        }, TaskSchedule.tick(2), TaskSchedule.tick(2));
    }
}