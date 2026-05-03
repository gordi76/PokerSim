package de.pokersim.adapters;

import java.util.List;

public final class GameViewModel {
    private final String gameId;
    private final String phase;
    private final List<String> players;
    private final List<String> communityCards;
    private final String communityLabel;
    private final String pot;

    public GameViewModel(
            String gameId,
            String phase,
            List<String> players,
            List<String> communityCards,
            String communityLabel,
            String pot
    ) {
        this.gameId = gameId;
        this.phase = phase;
        this.players = List.copyOf(players);
        this.communityCards = List.copyOf(communityCards);
        this.communityLabel = communityLabel;
        this.pot = pot;
    }

    public String gameId() {
        return gameId;
    }

    public String phase() {
        return phase;
    }

    public List<String> players() {
        return players;
    }

    public List<String> communityCards() {
        return communityCards;
    }

    public String communityLabel() {
        return communityLabel;
    }

    public String pot() {
        return pot;
    }
}