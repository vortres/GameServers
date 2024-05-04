package me.vortres.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class MessageHelper {
    public static final TextColor BLUE_COLOR = TextColor.color(0x3D6DDB);
    public static final TextColor BLUE_ISH_COLOR = TextColor.color(0x8C8CFF);
    public static final TextColor GRAY_COLOR = TextColor.color(0xA5A5A5);
    public static final TextColor RED_COLOR = TextColor.color(0xE6143C);
    public static final TextColor RED_ISH_COLOR = TextColor.color(0xFF8C8C);

    public static void info(Audience audience, String message) {
        info(audience, Component.text(message));
    }

    public static void info(Audience audience, Component message) {
        audience.sendMessage(
                Component.text("> ").color(TextColor.color(0x3D6DDB)).decoration(TextDecoration.BOLD, true)
                .append(message.color(GRAY_COLOR))
        );
    }

    public static void warn(Audience audience, String message) {
        warn(audience, Component.text(message));
    }

    public static void warn(Audience audience, Component message) {
        audience.sendMessage(Component.text("! ", RED_COLOR, TextDecoration.BOLD)
                .append(message.color(NamedTextColor.GRAY)));
    }

    public static void fancyTitle(Audience audience, String message) {
        fancyTitle(audience, Component.text(message));
    }

    public static void fancyTitle(Audience audience, Component message) {
        audience.showTitle(Title.title(message, Component.empty()));
        audience.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.BLOCK, 1, 1), Sound.Emitter.self());
    }

    public static CompletableFuture<Void> countdown(Audience audience, int from) {
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        final AtomicInteger countdown = new AtomicInteger(from);
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            final int count = countdown.getAndDecrement();
            if (count <= 0) {
                completableFuture.complete(null);
                return TaskSchedule.stop();
            }

            audience.showTitle(Title.title(Component.text(count, NamedTextColor.GREEN), Component.empty()));
            audience.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.BLOCK, 1, 1), Sound.Emitter.self());

            return TaskSchedule.seconds(1);
        });

        return completableFuture;
    }
}