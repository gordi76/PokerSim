package de.pokersim.adapters;

import de.pokersim.domain.Card;
import de.pokersim.domain.Game;
import de.pokersim.domain.GamePhase;
import de.pokersim.domain.Player;

import java.util.ArrayList;
import java.util.List;

public final class GamePresenter {

    public GameViewModel present(Game game) {
        boolean revealHoleCards = game.phase() == GamePhase.SHOWDOWN
                || game.phase() == GamePhase.FINISHED;

        List<String> players = new ArrayList<>();
        for (Player player : game.players()) {
            String hand = revealHoleCards
                    ? String.join(" ", formatCards(player.holeCards()))
                    : "?? ??";
            String status = player.hasFolded() ? " [folded]" : "";
            players.add(player.name() + status
                    + "  |  chips: " + player.chips().amount()
                    + "  |  hand: " + hand);
        }

        return new GameViewModel(
                game.id().value(),
                game.phase().name(),
                players,
                formatCards(game.communityCards()),
                communityLabel(game),
                game.pot().toString()
        );
    }

    private String communityLabel(Game game) {
        List<Card> cards = game.communityCards();
        if (cards.isEmpty()) {
            return "(no community cards yet)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Flop: ").append(String.join(" ", formatCards(cards.subList(0, 3))));
        if (cards.size() >= 4) {
            sb.append("  |  Turn: ").append(formatCards(cards.subList(3, 4)).get(0));
        }
        if (cards.size() >= 5) {
            sb.append("  |  River: ").append(formatCards(cards.subList(4, 5)).get(0));
        }
        return sb.toString();
    }

    public List<String> formatCards(List<Card> cards) {
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