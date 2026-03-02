package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.Chips;
import de.pokersim.domain.Player;
import de.pokersim.domain.PlayerId;
import de.pokersim.infrastructure.GameRepository;
import de.pokersim.infrastructure.RandomSource;

import java.util.List;
import java.util.Objects;

public final class GameService {
    private final StartGameUseCase startGameUseCase;
    private final AdvancePhaseUseCase advancePhaseUseCase;
    private final PlaceBetUseCase placeBetUseCase;
    private final FoldUseCase foldUseCase;
    private final ShowdownUseCase showdownUseCase;
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository, RandomSource randomSource) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
        this.startGameUseCase = new StartGameUseCase(gameRepository, randomSource);
        this.advancePhaseUseCase = new AdvancePhaseUseCase(gameRepository);
        this.placeBetUseCase = new PlaceBetUseCase(gameRepository);
        this.foldUseCase = new FoldUseCase(gameRepository);
        this.showdownUseCase = new ShowdownUseCase(gameRepository);
    }

    public GameId startGame(List<String> playerNames) {
        return startGameUseCase.start(playerNames);
    }

    public Game advancePhase(GameId gameId) {
        return advancePhaseUseCase.advance(gameId);
    }
    public Game placeBet(GameId gameId, PlayerId playerId, Chips amount) {
        return placeBetUseCase.placeBet(gameId, playerId, amount);
    }

    public Game fold(GameId gameId, PlayerId playerId) {
        return foldUseCase.fold(gameId, playerId);
    }

    public Player showdown(GameId gameId) {
        return showdownUseCase.showdown(gameId);
    }
    public Game getGame(GameId gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));
    }
}