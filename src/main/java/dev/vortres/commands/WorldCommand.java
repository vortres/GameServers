package me.vortres.commands;

import me.vortres.instance.LobbyInstance;
import me.vortres.instance.VanillaInstance;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class WorldCommand extends Command {

    public WorldCommand() {
        super("world");
        setCondition(Conditions::playerOnly);
        setDefaultExecutor((source, args) -> source.sendMessage("Usage: /world <terrain|flat|void>"));

        var option = ArgumentType.Word("option").from("terrain", "flat", "void");

        addSyntax(this::execute, option);
    }

    private void execute(CommandSender source, CommandContext context) {
        Player player = (Player) source;
        String option = context.get("option");

        switch (option) {
            case "terrain":
                player.setInstance(VanillaInstance.INSTANCE);
                player.setRespawnPoint(new Pos(0.5, 70, 0.5));
                break;
            case "flat":
                player.setInstance(LobbyInstance.INSTANCE);
                player.setRespawnPoint(new Pos(0.5, 70, 0.5));
                break;
            case "void":
                player.setInstance(LobbyInstance.INSTANCE);
                player.setRespawnPoint(new Pos(0.5, 70, 0.5));
                break;
        }
    }
}