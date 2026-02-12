package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.infrastructure.GameRepository;

import java.util.Objects;

public final class AdvancePhaseUseCase {
    private final GameRepository gameRepository;

    public AdvancePhaseUseCase(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
    }

    public Game advance(GameId gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));

        game.advancePhase();
        gameRepository.save(game);

        return game;
    }
}