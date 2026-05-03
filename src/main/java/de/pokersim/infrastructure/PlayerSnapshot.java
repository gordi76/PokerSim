package de.pokersim.infrastructure;

import java.util.List;
import java.util.Objects;

public final class PlayerSnapshot {
    private final String playerId;
    private final String name;
    private final int chips;
    private final boolean folded;
    private final List<CardSnapshot> holeCards;

    public PlayerSnapshot(
            String playerId,
            String name,
            int chips,
            boolean folded,
            List<CardSnapshot> holeCards
    ) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("playerId must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (chips < 0) {
            throw new IllegalArgumentException("chips must not be negative");
        }

        this.playerId = playerId;
        this.name = name;
        this.chips = chips;
        this.folded = folded;
        this.holeCards = List.copyOf(Objects.requireNonNull(holeCards, "holeCards must not be null"));
    }

    public String playerId() {
        return playerId;
    }

    public String name() {
        return name;
    }

    public int chips() {
        return chips;
    }

    public boolean folded() {
        return folded;
    }

    public List<CardSnapshot> holeCards() {
        return holeCards;
    }

    public int holeCardCount() {
        return holeCards.size();
    }
}