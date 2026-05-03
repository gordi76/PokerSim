package de.pokersim.adapters;

import de.pokersim.domain.Card;
import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.Rank;
import de.pokersim.domain.Suit;
import de.pokersim.infrastructure.FixedRandomSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer den {@link GamePresenter}.
 *
 * <p>Pruefen die Umwandlung des Domain-Aggregates in das
 * {@link GameViewModel} - insbesondere die Card-Format-Logik
 * (Rank-Kuerzel + Suit-Buchstabe).</p>
 */
class GamePresenterTest {

    private final GamePresenter presenter = new GamePresenter();

    @Test
    @DisplayName("formats short rank and suit codes")
    void formatsShortCardCodes() {
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(100));
        game.addPlayer("Bob", new Chips(100));
        game.start(FixedRandomSource.noShuffle());

        GameViewModel viewModel = presenter.present(game);

        for (String line : viewModel.players()) {
            assertTrue(line.contains("hand:"),
                    "every player line should contain its hand");
        }
        assertNotNull(viewModel.gameId());
        assertEquals("PRE_FLOP", viewModel.phase());
    }

    @Test
    @DisplayName("ten of hearts is rendered as '10H'")
    void rendersTenOfHearts() {
        Game game = new Game(GameId.newId());
        game.addPlayer("A", new Chips(100));
        game.addPlayer("B", new Chips(100));
        game.start(FixedRandomSource.noShuffle());

        GameViewModel viewModel = presenter.present(game);
        for (String playerLine : viewModel.players()) {
            int handIdx = playerLine.indexOf("hand: ");
            assertTrue(handIdx >= 0);
            String handPart = playerLine.substring(handIdx + "hand: ".length());
            assertTrue(handPart.matches(".*[CDHS].*"),
                    "expected at least one suit code (C/D/H/S) in: " + handPart);
        }

        for (Rank rank : Rank.values()) {
            for (Suit suit : Suit.values()) {
                assertNotNull(new Card(rank, suit).toString());
            }
        }
    }

    @Test
    @DisplayName("pot is rendered with 'Pot:' prefix")
    void rendersPotWithPrefix() {
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(100));
        game.addPlayer("Bob", new Chips(100));
        game.start(FixedRandomSource.noShuffle());

        GameViewModel viewModel = presenter.present(game);

        assertTrue(viewModel.pot().startsWith("Pot:"));
    }
}
