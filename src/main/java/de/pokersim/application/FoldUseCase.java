package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.PlayerId;
import de.pokersim.infrastructure.GameRepository;

import java.util.Objects;

/**
 * Use Case: ein Spieler steigt aus der laufenden Runde aus (Fold).
 *
 * Eigenstaendige Use-Case-Klasse, damit jede Spieler-Aktion (Bet, Fold,
 * spaeter z.B. Check) eine klare, einzeln testbare Verantwortung hat
 * (Single Responsibility Principle).
 */
public final class FoldUseCase {

    private final GameRepository gameRepository;

    public FoldUseCase(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
    }

    public Game fold(GameId gameId, PlayerId playerId) {
        Objects.requireNonNull(gameId, "gameId must not be null");
        Objects.requireNonNull(playerId, "playerId must not be null");

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));

        game.fold(playerId);
        gameRepository.save(game);

        return game;
    }
}
