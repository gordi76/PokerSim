package de.pokersim.domain;

import de.pokersim.infrastructure.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Deck {
    private final List<Card> cards;

    private Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public static Deck standard52CardDeck() {
        List<Card> createdCards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                createdCards.add(new Card(rank, suit));
            }
        }

        return new Deck(createdCards);
    }

    public void shuffle(RandomSource randomSource) {
        Objects.requireNonNull(randomSource, "randomSource must not be null");

        for (int currentIndex = cards.size() - 1; currentIndex > 0; currentIndex--) {
            int randomIndex = randomSource.nextInt(currentIndex + 1);
            Collections.swap(cards, currentIndex, randomIndex);
        }
    }

    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("deck is empty");
        }
        return cards.remove(0);
    }

    public int remainingCards() {
        return cards.size();
    }

    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }
}