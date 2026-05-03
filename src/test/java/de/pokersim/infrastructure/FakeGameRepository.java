package de.pokersim.infrastructure;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class FakeGameRepository implements GameRepository {

    private final Map<GameId, Game> store = new HashMap<>();
    private int saveCount;
    private int findCount;
    private Game lastSaved;

    @Override
    public void save(Game game) {
        store.put(game.id(), game);
        lastSaved = game;
        saveCount++;
    }

    @Override
    public Optional<Game> findById(GameId id) {
        findCount++;
        return Optional.ofNullable(store.get(id));
    }

    public int saveCount() {
        return saveCount;
    }

    public int findCount() {
        return findCount;
    }

    public Game lastSaved() {
        return lastSaved;
    }
}
