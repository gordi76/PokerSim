package de.pokersim.domain;

import de.pokersim.infrastructure.FixedRandomSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    @DisplayName("a fresh standard deck has 52 cards")
    void freshDeckHas52Cards() {
        Deck deck = Deck.standard52CardDeck();

        assertEquals(52, deck.remainingCards());
    }

    @Test
    @DisplayName("drawing reduces the remaining cards")
    void drawReducesRemaining() {
        Deck deck = Deck.standard52CardDeck();

        Card first = deck.draw();
        Card second = deck.draw();

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
        assertEquals(50, deck.remainingCards());
    }

    @Test
    @DisplayName("draw on an empty deck throws")
    void drawOnEmptyDeckThrows() {
        Deck deck = Deck.standard52CardDeck();
        for (int i = 0; i < 52; i++) {
            deck.draw();
        }

        assertThrows(IllegalStateException.class, deck::draw);
    }

    @Test
    @DisplayName("FixedRandomSource produces a reproducible shuffle")
    void shuffleIsReproducible() {
        Deck deckA = Deck.standard52CardDeck();
        Deck deckB = Deck.standard52CardDeck();

        deckA.shuffle(new FixedRandomSource(7, 13, 21, 5, 33));
        deckB.shuffle(new FixedRandomSource(7, 13, 21, 5, 33));

        for (int i = 0; i < 52; i++) {
            assertEquals(deckA.draw(), deckB.draw(),
                    "two decks shuffled with identical seeds must draw equally");
        }
    }
}
