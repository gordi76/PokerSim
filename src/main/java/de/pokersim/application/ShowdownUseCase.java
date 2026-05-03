package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.HandEvaluator;
import de.pokersim.domain.Player;
import de.pokersim.domain.TexasHoldemHandEvaluator;
import de.pokersim.infrastructure.GameRepository;

import java.util.Objects;

public final class ShowdownUseCase {

    private final GameRepository gameRepository;
    private final HandEvaluator handEvaluator;

    public ShowdownUseCase(GameRepository gameRepository) {
        this(gameRepository, new TexasHoldemHandEvaluator());
    }

    public ShowdownUseCase(GameRepository gameRepository, HandEvaluator handEvaluator) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
        this.handEvaluator = Objects.requireNonNull(handEvaluator, "handEvaluator must not be null");
    }

    /**
     * @param gameId Spiel im Showdown- oder Finished-Zustand
     * @return Gewinner-Spieler
     */
    public Player showdown(GameId gameId) {
        Objects.requireNonNull(gameId, "gameId must not be null");

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));

        Player winner = game.determineWinner(handEvaluator);
        game.payOutTo(winner);
        gameRepository.save(game);

        return winner;
    }
}
