package de.pokersim.domain;

import java.util.List;

public interface HandEvaluator {
    HandRank evaluate(List<Card> cards);
}