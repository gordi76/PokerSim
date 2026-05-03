package de.pokersim.infrastructure;

import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileGameRepositoryTest {

    @Test
    @DisplayName("save writes a UTF-8 summary file")
    void saveWritesUtf8Summary(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("pokersim-game.txt");
        FileGameRepository repository = new FileGameRepository(file);

        Game game = new Game(GameId.of("test-game"));
        game.addPlayer("Alice", new Chips(100));

        repository.save(game);

        assertTrue(Files.exists(file));
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("gameId=test-game"));
        assertTrue(content.contains("phase=WAITING_FOR_PLAYERS"));
        assertTrue(content.contains("players=1"));
    }

    @Test
    @DisplayName("findById returns the last saved game")
    void findByIdReturnsLastSavedGame(@TempDir Path tempDir) {
        FileGameRepository repository = new FileGameRepository(
                tempDir.resolve("pokersim-game.txt"));
        Game game = new Game(GameId.of("kept"));

        repository.save(game);

        assertTrue(repository.findById(GameId.of("kept")).isPresent());
        assertTrue(repository.findById(GameId.of("other")).isEmpty());
    }
}
