package de.pokersim.infrastructure;

import java.util.ArrayList;
import java.util.List;

public final class GameSnapshotParser {
    private static final String HEADER = "POKERSIM_GAME_SNAPSHOT";

    public GameSnapshot parse(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("snapshot content must not be blank");
        }

        String[] lines = content.split("\\R");

        if (lines.length == 0 || !HEADER.equals(lines[0])) {
            throw new IllegalArgumentException("invalid snapshot header");
        }

        String gameId = null;
        String phase = null;
        Integer pot = null;
        List<CardSnapshot> communityCards = new ArrayList<>();
        List<PlayerSnapshot> players = new ArrayList<>();

        for (int index = 1; index < lines.length; index++) {
            String line = lines[index];

            if (line.isBlank()) {
                continue;
            }

            if (line.startsWith("gameId=")) {
                gameId = line.substring("gameId=".length());
                continue;
            }

            if (line.startsWith("phase=")) {
                phase = line.substring("phase=".length());
                continue;
            }

            if (line.startsWith("pot=")) {
                pot = Integer.parseInt(line.substring("pot=".length()));
                continue;
            }

            if (line.startsWith("communityCards=")) {
                communityCards = parseCards(line.substring("communityCards=".length()));
                continue;
            }

            if (line.startsWith("player=")) {
                players.add(parsePlayer(line.substring("player=".length())));
                continue;
            }

            throw new IllegalArgumentException("unknown snapshot line: " + line);
        }

        if (gameId == null || phase == null || pot == null) {
            throw new IllegalArgumentException("snapshot misses required values");
        }

        return new GameSnapshot(gameId, phase, pot, players, communityCards);
    }

    private PlayerSnapshot parsePlayer(String playerLine) {
        List<String> parts = splitEscaped(playerLine, '|');

        if (parts.size() != 5) {
            throw new IllegalArgumentException("invalid player line: " + playerLine);
        }

        String playerId = unescape(parts.get(0));
        String name = unescape(parts.get(1));
        int chips = Integer.parseInt(parts.get(2));
        boolean folded = Boolean.parseBoolean(parts.get(3));
        List<CardSnapshot> holeCards = parseCards(parts.get(4));

        return new PlayerSnapshot(playerId, name, chips, folded, holeCards);
    }

    private List<CardSnapshot> parseCards(String cardLine) {
        List<CardSnapshot> cards = new ArrayList<>();

        if (cardLine == null || cardLine.isBlank()) {
            return cards;
        }

        String[] tokens = cardLine.split(",");

        for (String token : tokens) {
            if (!token.isBlank()) {
                cards.add(CardSnapshot.fromStorageToken(token));
            }
        }

        return cards;
    }

    private List<String> splitEscaped(String text, char separator) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            if (escaping) {
                current.append('\\').append(character);
                escaping = false;
                continue;
            }

            if (character == '\\') {
                escaping = true;
                continue;
            }

            if (character == separator) {
                parts.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(character);
        }

        if (escaping) {
            current.append('\\');
        }

        parts.add(current.toString());
        return parts;
    }

    private String unescape(String value) {
        return value
                .replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\|", "|")
                .replace("\\\\", "\\");
    }
}