package dev.vortres.lobby.features;

import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Feature {
    void hook(@NotNull EventNode<InstanceEvent> node);
}
