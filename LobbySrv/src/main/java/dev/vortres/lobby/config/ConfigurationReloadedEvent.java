package dev.vortres.lobby.config;

import net.minestom.server.event.Event;

public record ConfigurationReloadedEvent(Config previousConfig, Config currentConfig) implements Event {
}
