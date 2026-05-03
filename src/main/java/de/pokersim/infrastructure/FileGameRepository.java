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
    private final GameSnapshotMapper snapshotMapper;
    private final GameSnapshotSerializer serializer;
    private final GameSnapshotParser parser;

    private Game lastSavedGame;

    public FileGameRepository(Path storageFile) {
        this.storageFile = storageFile;
        this.snapshotMapper = new GameSnapshotMapper();
        this.serializer = new GameSnapshotSerializer();
        this.parser = new GameSnapshotParser();
    }

    @Override
    public void save(Game game) {
        this.lastSavedGame = game;

        GameSnapshot snapshot = snapshotMapper.toSnapshot(game);
        String content = serializer.serialize(snapshot);

        try {
            Path parent = storageFile.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(storageFile, content, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("could not write game file", exception);
        }
    }

    @Override
    public Optional<Game> findById(GameId id) {
        if (lastSavedGame != null && lastSavedGame.id().equals(id)) {
            return Optional.of(lastSavedGame);
        }

        if (!Files.exists(storageFile)) {
            return Optional.empty();
        }

        GameSnapshot snapshot = readSnapshot();

        if (!snapshot.gameId().equals(id.value())) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    public Optional<GameSnapshot> findSnapshotById(GameId id) {
        if (!Files.exists(storageFile)) {
            return Optional.empty();
        }

        GameSnapshot snapshot = readSnapshot();

        if (!snapshot.gameId().equals(id.value())) {
            return Optional.empty();
        }

        return Optional.of(snapshot);
    }

    public Path storageFile() {
        return storageFile;
    }

    private GameSnapshot readSnapshot() {
        try {
            String content = Files.readString(storageFile, StandardCharsets.UTF_8);
            return parser.parse(content);
        } catch (IOException exception) {
            throw new IllegalStateException("could not read game file", exception);
        }
    }
}