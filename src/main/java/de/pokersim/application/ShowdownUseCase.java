package de.pokersim.application;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.HandEvaluator;
import de.pokersim.domain.Player;
import de.pokersim.domain.TexasHoldemHandEvaluator;
import de.pokersim.infrastructure.GameRepository;

import java.util.Objects;

/**
 * Use Case: Showdown durchfuehren - Sieger ermitteln und Pot auszahlen.
 *
 * Trennt die "wann?"-Frage (Anwendungs-Schicht) von der "wie?"-Frage
 * (Domain-Schicht): Die Domain-Klasse {@link Game} entscheidet, wer
 * gewinnt; dieser Use Case orchestriert Persistenz und Strategie-Auswahl.
 *
 * <p>Nutzt das Strategy-Pattern: ein {@link HandEvaluator} wird per
 * Konstruktor injiziert, sodass Tests eine deterministische Variante
 * (zum Beispiel {@code FixedHandEvaluator}) einsetzen koennen.</p>
 */
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
     * Ermittelt den Gewinner und zahlt den Pot aus.
     *
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
