package de.pokersim.domain;

public enum HandRank {
    HIGH_CARD(1),
    PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    STRAIGHT(5),
    FLUSH(6),
    FULL_HOUSE(7),
    FOUR_OF_A_KIND(8),
    STRAIGHT_FLUSH(9);

    private final int strength;

    HandRank(int strength) {
        this.strength = strength;
    }

    public int strength() {
        return strength;
    }

    public boolean beats(HandRank other) {
        return this.strength > other.strength;
    }
}