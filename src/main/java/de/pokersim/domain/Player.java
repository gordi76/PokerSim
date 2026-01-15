package de.pokersim.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Player {
    private final PlayerId id;
    private final String name;
    private Chips chips;
    private final List<Card> holeCards;
    private boolean folded;

    public Player(PlayerId id, String name, Chips chips) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("player name must not be blank");
        }
        this.name = name;
        this.chips = Objects.requireNonNull(chips, "chips must not be null");
        this.holeCards = new ArrayList<>();
        this.folded = false;
    }

    public PlayerId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Chips chips() {
        return chips;
    }

    public List<Card> holeCards() {
        return Collections.unmodifiableList(holeCards);
    }

    public boolean hasFolded() {
        return folded;
    }

    public void receive(Card card) {
        if (holeCards.size() >= 2) {
            throw new IllegalStateException("a player cannot have more than two hole cards");
        }
        holeCards.add(Objects.requireNonNull(card, "card must not be null"));
    }

    public Chips bet(Chips amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        chips = chips.minus(amount);
        return amount;
    }

    public void win(Chips amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        chips = chips.plus(amount);
    }

    public void fold() {
        folded = true;
    }

    public void resetForNextRound() {
        holeCards.clear();
        folded = false;
    }

    @Override
    public String toString() {
        return name + " (" + chips + ")";
    }
}