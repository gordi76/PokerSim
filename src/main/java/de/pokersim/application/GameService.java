package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.infrastructure.GameRepository;
import de.pokersim.infrastructure.RandomSource;

import java.util.List;

public final class GameService {
    private final StartGameUseCase startGameUseCase;
    private final AdvancePhaseUseCase advancePhaseUseCase;
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository, RandomSource randomSource) {
        this.gameRepository = gameRepository;
        this.startGameUseCase = new StartGameUseCase(gameRepository, randomSource);
        this.advancePhaseUseCase = new AdvancePhaseUseCase(gameRepository);
    }

    public GameId startGame(List<String> playerNames) {
        return startGameUseCase.start(playerNames);
    }

    public Game advancePhase(GameId gameId) {
        return advancePhaseUseCase.advance(gameId);
    }

    public Game getGame(GameId gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));
    }
}