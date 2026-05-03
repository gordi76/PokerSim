package de.pokersim.infrastructure;

import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryGameRepositoryTest {

    @Test
    @DisplayName("returns empty optional for unknown id")
    void returnsEmptyForUnknownId() {
        InMemoryGameRepository repo = new InMemoryGameRepository();

        Optional<Game> result = repo.findById(GameId.of("does-not-exist"));

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("retrieves a previously saved game by id")
    void retrievesSavedGame() {
        InMemoryGameRepository repo = new InMemoryGameRepository();
        Game game = new Game(GameId.newId());
        game.addPlayer("Alice", new Chips(1000));

        repo.save(game);

        Optional<Game> found = repo.findById(game.id());
        assertTrue(found.isPresent());
        assertSame(game, found.get());
    }

    @Test
    @DisplayName("size grows with each distinct saved game")
    void sizeGrowsForDistinctIds() {
        InMemoryGameRepository repo = new InMemoryGameRepository();
        Game first = new Game(GameId.newId());
        Game second = new Game(GameId.newId());

        repo.save(first);
        repo.save(second);

        assertEquals(2, repo.size());
    }
}
