package de.pokersim.domain;

public final class Chips implements Comparable<Chips> {
    private final int amount;

    public Chips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("chips must not be negative");
        }
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }

    public Chips plus(Chips other) {
        return new Chips(this.amount + other.amount);
    }

    public Chips minus(Chips other) {
        if (other.amount > this.amount) {
            throw new IllegalArgumentException("not enough chips");
        }
        return new Chips(this.amount - other.amount);
    }

    public boolean isZero() {
        return amount == 0;
    }

    @Override
    public int compareTo(Chips other) {
        return Integer.compare(this.amount, other.amount);
    }

    @Override
    public String toString() {
        return amount + " chips";
    }
}