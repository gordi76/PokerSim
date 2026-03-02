package de.pokersim.application;

import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.PlayerId;
import de.pokersim.infrastructure.GameRepository;

import java.util.Objects;

/**
 * Use Case: ein Spieler setzt Chips in den Pot.
 *
 * Diese Use-Case-Klasse kapselt die Anwendungs-Logik fuer eine
 * einzelne Bet-Aktion und haelt damit das Aggregate Root {@link Game}
 * frei von Persistenz-Wissen. Die Klasse erfuellt die Dependency Rule
 * von Clean Architecture: sie haengt vom abstrakten {@link GameRepository}
 * ab, nicht von einer konkreten Implementierung.
 */
public final class PlaceBetUseCase {

    private final GameRepository gameRepository;

    public PlaceBetUseCase(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository, "gameRepository must not be null");
    }

    /**
     * Fuehrt einen Einsatz aus und persistiert das Spiel.
     *
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
