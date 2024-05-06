package dev.vortres.lobby.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.Collection;

import static net.minestom.server.command.builder.arguments.ArgumentType.Float;
import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;

public class FindCommand extends Command {
    public FindCommand() {
        super("find");

        this.addSyntax(
                this::executorEntity,
                Literal("entity"),
                Float("range")
        );
    }

    private void executorEntity(CommandSender sender, CommandContext context) {
        Player player = (Player) sender;
        float range = context.get("range");

        Collection<Entity> entities = player.getInstance().getNearbyEntities(player.getPosition(), range);

        player.sendMessage(Component.text("Search result", TextColor.color(0x7575FF))
                .append(Component.text(":", TextColor.color(0x5F5F5F)))
        );

        for (Entity entity : entities) {
            player.sendMessage(Component.text(" - ", TextColor.color(0x9595FF))
                    .append(Component.text(String.valueOf(entity.getEntityType()), TextColor.color(0x8C8C8C)))
                    .append(Component.text(":", TextColor.color(0x5F5F5F)))
            );
            player.sendMessage(Component.text("   - Meta: ", TextColor.color(0x5F5F5F))
                    .append(Component.text(String.valueOf("    " + entity.getEntityMeta()), TextColor.color(0x8C8C8C)))
                    .append(Component.text(""))
            );
            player.sendMessage(Component.text("   - Position: ", TextColor.color(0x5F5F5F))
                    .append(Component.text(String.valueOf("    " + entity.getPosition()), TextColor.color(0x8C8C8C)))
                    .append(Component.text(""))
            );
            player.sendMessage("");
        }
    }
}