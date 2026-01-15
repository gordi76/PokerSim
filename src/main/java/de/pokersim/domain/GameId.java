package de.pokersim.domain;

import java.util.Objects;
import java.util.UUID;

public final class GameId {
    private final String value;

    private GameId(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static GameId newId() {
        return new GameId(UUID.randomUUID().toString());
    }

    public static GameId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("game id must not be blank");
        }
        return new GameId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GameId gameId)) {
            return false;
        }
        return value.equals(gameId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}