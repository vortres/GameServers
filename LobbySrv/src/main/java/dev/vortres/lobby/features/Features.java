package dev.vortres.lobby.features;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.*;

public final class Features {
    public static @NotNull Feature bow(BiFunction<Entity, Double, EntityProjectile> projectileGenerator) {
        return new BowFeature(projectileGenerator);
    }

    public static @NotNull Feature functionalItem(Predicate<ItemStack> trigger, Consumer<Player> consumer, long cooldown) {
        return new FunctionalItemFeature(trigger, consumer, cooldown);
    }
}
