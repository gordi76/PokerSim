package de.pokersim.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer den {@link TexasHoldemHandEvaluator}.
 *
 * <p>Decken alle 9 {@link HandRank}-Faelle ab und erfuellen damit den
 * ATRIP-Aspekt "Thorough": jede Verzweigung im Evaluator wird durch
 * mindestens einen Test angesteuert.</p>
 */
class TexasHoldemHandEvaluatorTest {

    private final HandEvaluator evaluator = new TexasHoldemHandEvaluator();

    @Test
    @DisplayName("recognises a high card hand")
    void recognisesHighCard() {
        List<Card> cards = List.of(
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.JACK, Suit.HEARTS)
        );

        assertEquals(HandRank.HIGH_CARD, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises a single pair")
    void recognisesPair() {
        List<Card> cards = List.of(
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.JACK, Suit.HEARTS)
        );

        assertEquals(HandRank.PAIR, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises two pairs")
    void recognisesTwoPair() {
        List<Card> cards = List.of(
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.JACK, Suit.HEARTS)
        );

        assertEquals(HandRank.TWO_PAIR, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises three of a kind")
    void recognisesThreeOfAKind() {
        List<Card> cards = List.of(
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.JACK, Suit.HEARTS)
        );

        assertEquals(HandRank.THREE_OF_A_KIND, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises a straight")
    void recognisesStraight() {
        List<Card> cards = List.of(
                new Card(Rank.FOUR, Suit.CLUBS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.SIX, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.EIGHT, Suit.HEARTS)
        );

        assertEquals(HandRank.STRAIGHT, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises the wheel straight A-2-3-4-5")
    void recognisesWheelStraight() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.THREE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.SPADES),
                new Card(Rank.FIVE, Suit.HEARTS)
        );

        assertEquals(HandRank.STRAIGHT, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises a flush")
    void recognisesFlush() {
        List<Card> cards = List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS)
        );

        assertEquals(HandRank.FLUSH, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises a full house")
    void recognisesFullHouse() {
        List<Card> cards = List.of(
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.HEARTS)
        );

        assertEquals(HandRank.FULL_HOUSE, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises four of a kind")
    void recognisesFourOfAKind() {
        List<Card> cards = List.of(
                new Card(Rank.KING, Suit.CLUBS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.KING, Suit.DIAMONDS),
                new Card(Rank.KING, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.HEARTS)
        );

        assertEquals(HandRank.FOUR_OF_A_KIND, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("recognises a straight flush")
    void recognisesStraightFlush() {
        List<Card> cards = List.of(
                new Card(Rank.FOUR, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS)
        );

        assertEquals(HandRank.STRAIGHT_FLUSH, evaluator.evaluate(cards));
    }

    @Test
    @DisplayName("rejects hands with fewer than five cards")
    void rejectsTooFewCards() {
        List<Card> cards = List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS)
        );

        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(cards));
    }
}
