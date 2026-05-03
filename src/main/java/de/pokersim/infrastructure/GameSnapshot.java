package de.pokersim.infrastructure;

import java.util.List;
import java.util.Objects;

public final class GameSnapshot {
    private final String gameId;
    private final String phase;
    private final int pot;
    private final List<PlayerSnapshot> players;
    private final List<CardSnapshot> communityCards;

    public GameSnapshot(
            String gameId,
            String phase,
            int pot,
            List<PlayerSnapshot> players,
            List<CardSnapshot> communityCards
    ) {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("gameId must not be blank");
        }
        if (phase == null || phase.isBlank()) {
            throw new IllegalArgumentException("phase must not be blank");
        }

        this.gameId = gameId;
        this.phase = phase;
        this.pot = pot;
        this.players = List.copyOf(Objects.requireNonNull(players, "players must not be null"));
        this.communityCards = List.copyOf(Objects.requireNonNull(communityCards, "communityCards must not be null"));
    }

    public String gameId() {
        return gameId;
    }

    public String phase() {
        return phase;
    }

    public int pot() {
        return pot;
    }

    public List<PlayerSnapshot> players() {
        return players;
    }

    public List<CardSnapshot> communityCards() {
        return communityCards;
    }

    public int playerCount() {
        return players.size();
    }

    public int communityCardCount() {
        return communityCards.size();
    }

    public boolean hasCommunityCards() {
        return !communityCards.isEmpty();
    }
}