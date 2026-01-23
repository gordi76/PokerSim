package de.pokersim.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class TexasHoldemHandEvaluator implements HandEvaluator {

    @Override
    public HandRank evaluate(List<Card> cards) {
        if (cards == null || cards.size() < 5) {
            throw new IllegalArgumentException("at least five cards are required");
        }

        boolean flush = hasFlush(cards);
        boolean straight = hasStraight(cards);
        Map<Rank, Integer> rankCounts = countRanks(cards);

        if (flush && straight) {
            return HandRank.STRAIGHT_FLUSH;
        }
        if (containsCount(rankCounts, 4)) {
            return HandRank.FOUR_OF_A_KIND;
        }
        if (containsCount(rankCounts, 3) && containsCount(rankCounts, 2)) {
            return HandRank.FULL_HOUSE;
        }
        if (flush) {
            return HandRank.FLUSH;
        }
        if (straight) {
            return HandRank.STRAIGHT;
        }
        if (containsCount(rankCounts, 3)) {
            return HandRank.THREE_OF_A_KIND;
        }

        int pairs = numberOfPairs(rankCounts);
        if (pairs >= 2) {
            return HandRank.TWO_PAIR;
        }
        if (pairs == 1) {
            return HandRank.PAIR;
        }

        return HandRank.HIGH_CARD;
    }

    private Map<Rank, Integer> countRanks(List<Card> cards) {
        Map<Rank, Integer> counts = new HashMap<>();

        for (Card card : cards) {
            counts.put(card.rank(), counts.getOrDefault(card.rank(), 0) + 1);
        }

        return counts;
    }

    private boolean containsCount(Map<Rank, Integer> rankCounts, int searchedCount) {
        for (int count : rankCounts.values()) {
            if (count == searchedCount) {
                return true;
            }
        }

        return false;
    }

    private int numberOfPairs(Map<Rank, Integer> rankCounts) {
        int pairs = 0;

        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairs++;
            }
        }

        return pairs;
    }

    private boolean hasFlush(List<Card> cards) {
        Map<Suit, Integer> suitCounts = new HashMap<>();

        for (Card card : cards) {
            suitCounts.put(card.suit(), suitCounts.getOrDefault(card.suit(), 0) + 1);
        }

        for (int count : suitCounts.values()) {
            if (count >= 5) {
                return true;
            }
        }

        return false;
    }

    private boolean hasStraight(List<Card> cards) {
        List<Integer> values = uniqueSortedValues(cards);

        for (int index = 0; index <= values.size() - 5; index++) {
            int first = values.get(index);
            if (values.contains(first + 1)
                    && values.contains(first + 2)
                    && values.contains(first + 3)
                    && values.contains(first + 4)) {
                return true;
            }
        }

        return hasWheelStraight(values);
    }

    private List<Integer> uniqueSortedValues(List<Card> cards) {
        HashSet<Integer> uniqueValues = new HashSet<>();

        for (Card card : cards) {
            uniqueValues.add(card.rank().value());
        }

        List<Integer> values = new ArrayList<>(uniqueValues);
        values.sort(Integer::compareTo);
        return values;
    }

    private boolean hasWheelStraight(List<Integer> values) {
        return values.contains(Rank.ACE.value())
                && values.contains(Rank.TWO.value())
                && values.contains(Rank.THREE.value())
                && values.contains(Rank.FOUR.value())
                && values.contains(Rank.FIVE.value());
    }
}