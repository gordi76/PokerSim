package de.pokersim.infrastructure;

import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileGameRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void saveWritesUtf8SnapshotFile() throws Exception {
        Path storageFile = tempDir.resolve("game.txt");
        FileGameRepository repository = new FileGameRepository(storageFile);
        Game game = startedGame();

        repository.save(game);

        assertTrue(Files.exists(storageFile));

        String content = Files.readString(storageFile, StandardCharsets.UTF_8);

        assertTrue(content.contains("POKERSIM_GAME_SNAPSHOT"));
        assertTrue(content.contains("gameId=" + game.id().value()));
        assertTrue(content.contains("phase=" + game.phase().name()));
        assertTrue(content.contains("pot=" + game.pot().total().amount()));
        assertTrue(content.contains("communityCards="));
        assertTrue(content.contains("player="));
    }

    @Test
    void findByIdReturnsLastSavedGame() {
        Path storageFile = tempDir.resolve("game.txt");
        FileGameRepository repository = new FileGameRepository(storageFile);
        Game game = startedGame();

        repository.save(game);

        assertTrue(repository.findById(game.id()).isPresent());
        assertSame(game, repository.findById(game.id()).orElseThrow());
    }

    @Test
    void findByIdReturnsEmptyForUnknownGame() {
        Path storageFile = tempDir.resolve("game.txt");
        FileGameRepository repository = new FileGameRepository(storageFile);
        Game game = startedGame();

        repository.save(game);

        assertTrue(repository.findById(GameId.of("unknown-game")).isEmpty());
    }

    @Test
    void findSnapshotByIdReadsPersistedSnapshot() {
        Path storageFile = tempDir.resolve("game.txt");
        FileGameRepository repository = new FileGameRepository(storageFile);
        Game game = startedGame();

        repository.save(game);

        GameSnapshot snapshot = repository.findSnapshotById(game.id()).orElseThrow();

        assertEquals(game.id().value(), snapshot.gameId());
        assertEquals(game.phase().name(), snapshot.phase());
        assertEquals(game.players().size(), snapshot.playerCount());
        assertEquals(game.pot().total().amount(), snapshot.pot());
    }

    @Test
    void findSnapshotByIdReturnsEmptyForMissingFile() {
        Path storageFile = tempDir.resolve("missing-game.txt");
        FileGameRepository repository = new FileGameRepository(storageFile);

        assertTrue(repository.findSnapshotById(GameId.of("missing")).isEmpty());
    }

    private Game startedGame() {
        Game game = new Game(GameId.of("file-repository-test-game"));
        game.addPlayer("Alice", new Chips(1_000));
        game.addPlayer("Bob", new Chips(1_000));
        game.start(new PredictableRandomSource());
        return game;
    }

    private static final class PredictableRandomSource implements RandomSource {
        @Override
        public int nextInt(int upperBoundExclusive) {
            return 0;
        }
    }
}