package dev.vortres.lobby.instance;

import dev.vortres.lobby.util.MessageHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;

import java.nio.file.Path;

public final class LobbyInstance {
    public static final Instance INSTANCE;

    static  {
        final Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(
                FullbrightDimension.INSTANCE, new AnvilLoader(Path.of("lobby"))
        );

        instance.eventNode().addListener(AddEntityToInstanceEvent.class, event -> {
            if (!(event.getEntity() instanceof Player player)) return;

            if (player.getInstance() != null) player.scheduler().scheduleNextTick(() -> MessageHelper.fancyTitle(player, Component.text("Hello :>", TextColor.color(0x3D6DDB))));
            else onFirstSpawn(player);
        }).addListener(ItemDropEvent.class, event -> event.setCancelled(true));

        Map.create(instance, new Pos(2, 18, 9));
        instance.setTimeRate(0);
        for (NPC npc : NPC.spawnNPCs(instance)) {
            instance.eventNode().addListener(EntityAttackEvent.class, npc::handle)
                    .addListener(PlayerEntityInteractEvent.class, npc::handle);
        }

        INSTANCE = instance;
    }

    private static void onFirstSpawn(Player player) {
        player.sendPackets(Map.packets());
    }
}