package de.pokersim.infrastructure;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);

    Optional<Game> findById(GameId id);
}