package me.vortres.commands;

import me.vortres.util.MessageHelper;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class TestMessageHelper extends Command {
    public TestMessageHelper() {
        super("testmsg");
        addSyntax(this::execute);
    }

    private void execute(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {
        MessageHelper.info(commandSender, "Test message!");
        MessageHelper.warn(commandSender, "Test message!");
        MessageHelper.countdown(commandSender, 5).thenAccept(v -> {
            MessageHelper.fancyTitle(commandSender, "Hello :>");
        });
    }
}
