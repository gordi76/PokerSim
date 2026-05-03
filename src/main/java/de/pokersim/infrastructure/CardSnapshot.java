package de.pokersim.infrastructure;

public final class CardSnapshot {
    private final String rank;
    private final String suit;

    public CardSnapshot(String rank, String suit) {
        if (rank == null || rank.isBlank()) {
            throw new IllegalArgumentException("rank must not be blank");
        }
        if (suit == null || suit.isBlank()) {
            throw new IllegalArgumentException("suit must not be blank");
        }

        this.rank = rank;
        this.suit = suit;
    }

    public String rank() {
        return rank;
    }

    public String suit() {
        return suit;
    }

    public String asStorageToken() {
        return rank + ":" + suit;
    }

    public static CardSnapshot fromStorageToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("card token must not be blank");
        }

        String[] parts = token.split(":");

        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid card token: " + token);
        }

        return new CardSnapshot(parts[0], parts[1]);
    }
}