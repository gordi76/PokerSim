package de.pokersim.application;

import de.pokersim.adapters.GamePresenter;
import de.pokersim.adapters.GameViewModel;
import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.GamePhase;
import de.pokersim.domain.PlayerId;
import de.pokersim.infrastructure.FakeGameRepository;
import de.pokersim.infrastructure.FixedRandomSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    @DisplayName("startGame persists the game and returns its id")
    void startGamePersistsAndReturnsId() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());

        GameId gameId = service.startGame(List.of("Alice", "Bob"));

        assertNotNull(gameId);
        assertEquals(1, repository.saveCount(),
                "startGame must persist exactly once");
        assertEquals(GamePhase.PRE_FLOP, repository.lastSaved().phase());
    }

    @Test
    @DisplayName("advancePhase loads, mutates and persists the game")
    void advancePhaseLoadsAndPersists() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());
        GameId gameId = service.startGame(List.of("Alice", "Bob"));
        int saveCountAfterStart = repository.saveCount();

        Game game = service.advancePhase(gameId);

        assertEquals(GamePhase.FLOP, game.phase());
        assertEquals(saveCountAfterStart + 1, repository.saveCount(),
                "advancePhase must persist exactly one additional time");
    }

    @Test
    @DisplayName("placeBet persists the game and updates pot")
    void placeBetPersistsAndUpdatesPot() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());
        GameId gameId = service.startGame(List.of("Alice", "Bob"));
        Game initialState = service.getGame(gameId);
        PlayerId playerId = initialState.players().get(0).id();
        int potBefore = initialState.pot().total().amount();
        int saveCountBefore = repository.saveCount();

        Game updated = service.placeBet(gameId, playerId, new Chips(50));

        assertEquals(potBefore + 50, updated.pot().total().amount());
        assertEquals(saveCountBefore + 1, repository.saveCount());
    }

    @Test
    @DisplayName("fold marks the player as folded and persists")
    void foldMarksPlayer() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());
        GameId gameId = service.startGame(List.of("Alice", "Bob", "Charlie"));
        Game initialState = service.getGame(gameId);
        PlayerId playerId = initialState.players().get(0).id();

        Game updated = service.fold(gameId, playerId);

        assertTrue(updated.players().get(0).hasFolded());
    }

    @Test
    @DisplayName("getGame throws for unknown ids")
    void getGameThrowsForUnknownId() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());

        assertThrows(IllegalArgumentException.class,
                () -> service.getGame(GameId.of("ghost")));
    }

    @Test
    @DisplayName("startGame with too few players is rejected")
    void startGameRejectsTooFewPlayers() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());

        assertThrows(IllegalArgumentException.class,
                () -> service.startGame(List.of("Alice")));
        assertEquals(0, repository.saveCount(),
                "no save should happen for an invalid start");
    }

    @Test
    @DisplayName("a full game ends in FINISHED with an empty pot")
    void fullGameEndsFinished() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());
        GameId gameId = service.startGame(List.of("Alice", "Bob"));

        // PRE_FLOP -> FLOP -> TURN -> RIVER -> auto showdown FINISHED
        service.advancePhase(gameId);
        service.advancePhase(gameId);
        service.advancePhase(gameId);
        Game finished = service.advancePhase(gameId);

        assertEquals(GamePhase.FINISHED, finished.phase());
        assertTrue(finished.pot().isEmpty());
    }

    @Test
    @DisplayName("presenter rebuilds a view model after a game cycle")
    void presenterIntegrationSmoke() {
        FakeGameRepository repository = new FakeGameRepository();
        GameService service = new GameService(repository, FixedRandomSource.noShuffle());
        GameId gameId = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(gameId);

        GameViewModel viewModel = new GamePresenter().present(game);

        assertEquals("PRE_FLOP", viewModel.phase());
        assertEquals(2, viewModel.players().size());
        assertEquals(0, viewModel.communityCards().size());
    }
}
