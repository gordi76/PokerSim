package de.pokersim.domain;

import java.util.Objects;
import java.util.UUID;

public final class PlayerId {
    private final String value;

    private PlayerId(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static PlayerId newId() {
        return new PlayerId(UUID.randomUUID().toString());
    }

    public static PlayerId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("player id must not be blank");
        }
        return new PlayerId(value);
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
        if (!(other instanceof PlayerId playerId)) {
            return false;
        }
        return value.equals(playerId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}