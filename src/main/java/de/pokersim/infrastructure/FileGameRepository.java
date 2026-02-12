package de.pokersim.infrastructure;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class FileGameRepository implements GameRepository {
    private final Path storageFile;
    private Game lastSavedGame;

    public FileGameRepository(Path storageFile) {
        this.storageFile = storageFile;
    }

    @Override
    public void save(Game game) {
        this.lastSavedGame = game;
        String summary = "gameId=" + game.id()
                + System.lineSeparator()
                + "phase=" + game.phase()
                + System.lineSeparator()
                + "players=" + game.players().size()
                + System.lineSeparator()
                + "communityCards=" + game.communityCards().size()
                + System.lineSeparator()
                + "pot=" + game.pot().total().amount();

        try {
            Path parent = storageFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(storageFile, summary, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("could not write game file", exception);
        }
    }

    @Override
    public Optional<Game> findById(GameId id) {
        if (lastSavedGame != null && lastSavedGame.id().equals(id)) {
            return Optional.of(lastSavedGame);
        }

        return Optional.empty();
    }

    public Path storageFile() {
        return storageFile;
    }
}