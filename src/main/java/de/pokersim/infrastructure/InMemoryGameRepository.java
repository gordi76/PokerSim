package de.pokersim.infrastructure;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryGameRepository implements GameRepository {
    private final Map<GameId, Game> games;

    public InMemoryGameRepository() {
        this.games = new HashMap<>();
    }

    @Override
    public void save(Game game) {
        games.put(game.id(), game);
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return Optional.ofNullable(games.get(id));
    }

    public int size() {
        return games.size();
    }
}