package de.pokersim.infrastructure;

import java.util.StringJoiner;

public final class GameSnapshotSerializer {

    public String serialize(GameSnapshot snapshot) {
        StringBuilder builder = new StringBuilder();

        builder.append("POKERSIM_GAME_SNAPSHOT").append(System.lineSeparator());
        builder.append("gameId=").append(snapshot.gameId()).append(System.lineSeparator());
        builder.append("phase=").append(snapshot.phase()).append(System.lineSeparator());
        builder.append("pot=").append(snapshot.pot()).append(System.lineSeparator());
        builder.append("communityCards=").append(serializeCards(snapshot.communityCards())).append(System.lineSeparator());

        for (PlayerSnapshot player : snapshot.players()) {
            builder.append("player=")
                    .append(escape(player.playerId()))
                    .append("|")
                    .append(escape(player.name()))
                    .append("|")
                    .append(player.chips())
                    .append("|")
                    .append(player.folded())
                    .append("|")
                    .append(serializeCards(player.holeCards()))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String serializeCards(Iterable<CardSnapshot> cards) {
        StringJoiner joiner = new StringJoiner(",");

        for (CardSnapshot card : cards) {
            joiner.add(card.asStorageToken());
        }

        return joiner.toString();
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}