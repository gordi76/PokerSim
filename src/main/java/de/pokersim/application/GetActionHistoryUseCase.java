package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.PlayerAction;
import de.pokersim.infrastructure.GameRepository;

import java.util.List;
import java.util.Objects;

public final class GetActionHistoryUseCase {
    private final GameRepository gameRepository;

    public GetActionHistoryUseCase(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
    }

    public List<PlayerAction> historyFor(GameId gameId) {
        Objects.requireNonNull(gameId, "gameId must not be null");

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));

        return game.actionHistory().actions();
    }
}