package de.pokersim.domain;

import java.util.Objects;

public final class Pot {
    private Chips total;

    public Pot() {
        this.total = new Chips(0);
    }

    public Chips total() {
        return total;
    }

    public void add(Chips chips) {
        Objects.requireNonNull(chips, "chips must not be null");
        total = total.plus(chips);
    }

    public boolean isEmpty() {
        return total.isZero();
    }

    public Chips payOut() {
        Chips payout = total;
        total = new Chips(0);
        return payout;
    }

    @Override
    public String toString() {
        return "Pot: " + total;
    }
}