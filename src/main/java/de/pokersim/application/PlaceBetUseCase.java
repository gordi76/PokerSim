package de.pokersim.application;

import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.PlayerId;
import de.pokersim.infrastructure.GameRepository;

import java.util.Objects;

public final class PlaceBetUseCase {

    private final GameRepository gameRepository;

    public PlaceBetUseCase(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
    }

    /**
     * @param gameId   bereits bestehende Spiel-Id
     * @param playerId Spieler, der setzt
     * @param amount   Hoehe des Einsatzes
     * @return aktualisiertes Spiel
     * @throws IllegalArgumentException wenn das Spiel nicht existiert
     */
    public Game placeBet(GameId gameId, PlayerId playerId, Chips amount) {
        Objects.requireNonNull(gameId, "gameId must not be null");
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("unknown game id: " + gameId));

        game.placeBet(playerId, amount);
        gameRepository.save(game);

        return game;
    }
}
