package de.pokersim.application;

import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.GameRules;
import de.pokersim.infrastructure.GameRepository;
import de.pokersim.infrastructure.RandomSource;

import java.util.List;
import java.util.Objects;

public final class StartGameUseCase {

    private final GameRepository gameRepository;
    private final RandomSource randomSource;

    public StartGameUseCase(GameRepository gameRepository, RandomSource randomSource) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
        this.randomSource = Objects.requireNonNull(randomSource, "randomSource must not be null");
    }

    public GameId start(List<String> playerNames) {
        if (playerNames == null || playerNames.size() < 2) {
            throw new IllegalArgumentException("at least two player names are required");
        }

        Game game = new Game(GameId.newId());

        for (String playerName : playerNames) {
            game.addPlayer(playerName, new Chips(GameRules.INITIAL_CHIPS));
        }

        game.start(randomSource);
        gameRepository.save(game);

        return game.id();
    }
}