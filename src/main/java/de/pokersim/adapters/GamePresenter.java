package de.pokersim.adapters;

import de.pokersim.domain.Card;
import de.pokersim.domain.Game;
import de.pokersim.domain.Player;

import java.util.ArrayList;
import java.util.List;

public final class GamePresenter {

    public GameViewModel present(Game game) {
        List<String> players = new ArrayList<>();

        for (Player player : game.players()) {
            players.add(player.name()
                    + " | chips: " + player.chips().amount()
                    + " | hand: " + formatCards(player.holeCards()));
        }

        return new GameViewModel(
                game.id().value(),
                game.phase().name(),
                players,
                formatCards(game.communityCards()),
                game.pot().toString()
        );
    }

    private List<String> formatCards(List<Card> cards) {
        List<String> formattedCards = new ArrayList<>();

        for (Card card : cards) {
            formattedCards.add(formatCard(card));
        }

        return formattedCards;
    }

    private String formatCard(Card card) {
        return shortRank(card) + shortSuit(card);
    }

    private String shortRank(Card card) {
        return switch (card.rank()) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };
    }

    private String shortSuit(Card card) {
        return switch (card.suit()) {
            case CLUBS -> "C";
            case DIAMONDS -> "D";
            case HEARTS -> "H";
            case SPADES -> "S";
        };
    }
}