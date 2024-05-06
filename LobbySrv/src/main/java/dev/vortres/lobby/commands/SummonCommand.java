package dev.vortres.lobby.commands;

import dev.vortres.lobby.entity.ZombieEntity;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.instance.Instance;

public class SummonCommand extends Command {
    public SummonCommand() {
        super("summon");

        setDefaultExecutor((sender, context) -> {
            if (sender instanceof Player player) {
                final Instance instance = player.getInstance();
                final Pos position = player.getPosition();
                Entity entity = new ZombieEntity();
                entity.setInstance(instance, position);
            }
        });
    }
}