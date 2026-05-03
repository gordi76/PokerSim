package de.pokersim.infrastructure;

import de.pokersim.domain.Card;
import de.pokersim.domain.Game;
import de.pokersim.domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameSnapshotMapper {

    public GameSnapshot toSnapshot(Game game) {
        Objects.requireNonNull(game, "game must not be null");

        return new GameSnapshot(
                game.id().value(),
                game.phase().name(),
                game.pot().total().amount(),
                playerSnapshotsFrom(game.players()),
                cardSnapshotsFrom(game.communityCards())
        );
    }

    private List<PlayerSnapshot> playerSnapshotsFrom(List<Player> players) {
        List<PlayerSnapshot> snapshots = new ArrayList<>();

        for (Player player : players) {
            snapshots.add(playerSnapshotFrom(player));
        }

        return snapshots;
    }

    private PlayerSnapshot playerSnapshotFrom(Player player) {
        return new PlayerSnapshot(
                player.id().value(),
                player.name(),
                player.chips().amount(),
                player.hasFolded(),
                cardSnapshotsFrom(player.holeCards())
        );
    }

    private List<CardSnapshot> cardSnapshotsFrom(List<Card> cards) {
        List<CardSnapshot> snapshots = new ArrayList<>();

        for (Card card : cards) {
            snapshots.add(cardSnapshotFrom(card));
        }

        return snapshots;
    }

    private CardSnapshot cardSnapshotFrom(Card card) {
        return new CardSnapshot(card.rank().name(), card.suit().name());
    }
}