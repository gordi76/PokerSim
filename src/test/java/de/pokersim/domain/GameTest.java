package de.pokersim.domain;

import de.pokersim.infrastructure.FixedRandomSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    @DisplayName("starting a game deals two hole cards per player")
    void startDealsHoleCards() {
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(1000));
        game.addPlayer("Bob", new Chips(1000));

        game.start(FixedRandomSource.noShuffle());

        assertEquals(GamePhase.PRE_FLOP, game.phase());
        assertEquals(2, game.players().get(0).holeCards().size());
        assertEquals(2, game.players().get(1).holeCards().size());
    }

    @Test
    @DisplayName("starting requires at least two players")
    void startRequiresTwoPlayers() {
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(1000));

        assertThrows(IllegalStateException.class,
                () -> game.start(FixedRandomSource.noShuffle()));
    }

    @Test
    @DisplayName("advancing through phases reveals 5 community cards")
    void advancePhaseRevealsCommunityCards() {
        Game game = startedGame();

        game.advancePhase();
        assertEquals(GamePhase.FLOP, game.phase());
        assertEquals(3, game.communityCards().size());

        game.advancePhase();
        assertEquals(GamePhase.TURN, game.phase());
        assertEquals(4, game.communityCards().size());

        game.advancePhase();
        assertEquals(GamePhase.RIVER, game.phase());
        assertEquals(5, game.communityCards().size());
    }

    @Test
    @DisplayName("auto-showdown finishes the game when advancing from RIVER")
    void riverAutoShowdownFinishesGame() {
        Game game = startedGame();
        game.advancePhase(); // FLOP
        game.advancePhase(); // TURN
        game.advancePhase(); // RIVER

        game.advancePhase(); // RIVER -> auto showdown -> FINISHED

        assertEquals(GamePhase.FINISHED, game.phase());
        assertTrue(game.pot().isEmpty(), "pot must be paid out after auto-showdown");
    }

    @Test
    @DisplayName("placing a bet moves chips from player to pot")
    void placeBetMovesChipsToPot() {
        Game game = startedGame();
        Player player = game.players().get(0);
        int chipsBefore = player.chips().amount();
        int potBefore = game.pot().total().amount();

        game.placeBet(player.id(), new Chips(100));

        assertEquals(chipsBefore - 100, player.chips().amount());
        assertEquals(potBefore + 100, game.pot().total().amount());
    }

    @Test
    @DisplayName("folded players are excluded from the showdown")
    void foldedPlayersAreExcluded() {
        Game game = startedGame();
        Player loser = game.players().get(0);
        loser.fold();

        // advance to FINISHED
        game.advancePhase(); // FLOP
        game.advancePhase(); // TURN
        game.advancePhase(); // RIVER
        game.advancePhase(); // -> auto showdown / FINISHED

        boolean anyoneAboveStartingStack = game.players().stream()
                .filter(p -> !p.hasFolded())
                .anyMatch(p -> p.chips().amount() > 990);
        assertTrue(anyoneAboveStartingStack,
                "an active player must have received the pot");
    }

    @Test
    @DisplayName("cannot advance phase before the game has started")
    void cannotAdvanceBeforeStart() {
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(1000));
        game.addPlayer("Bob", new Chips(1000));

        assertThrows(IllegalStateException.class, game::advancePhase);
    }

    private Game startedGame() {
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(1000));
        game.addPlayer("Bob", new Chips(1000));
        game.start(FixedRandomSource.noShuffle());
        return game;
    }
}
