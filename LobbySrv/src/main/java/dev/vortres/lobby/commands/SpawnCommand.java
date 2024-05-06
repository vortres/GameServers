package dev.vortres.lobby.commands;

import dev.vortres.lobby.instance.LobbyInstance;
import dev.vortres.lobby.util.MessageHelper;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class SpawnCommand extends Command {
    public SpawnCommand() {
        super("spawn");

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) return;
            if (player.getInstance() == LobbyInstance.INSTANCE) {
                player.teleport(new Pos(0.5, 16, 0.5));
            } else {
                player.setInstance(LobbyInstance.INSTANCE);
                player.teleport(new Pos(0.5, 16, 0.5));
            }
            MessageHelper.fancyTitle(player, "Lobby");
        });
    }
}
