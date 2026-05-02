package de.pokersim.adapters;

import de.pokersim.application.GameService;
import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.Player;
import de.pokersim.domain.PlayerId;

import java.util.List;
import java.util.Objects;

public final class GameController {

    private final GameService gameService;
    private final GamePresenter gamePresenter;
    private GameId currentGameId;

    public GameController(GameService gameService, GamePresenter gamePresenter) {
        this.gameService = Objects.requireNonNull(gameService, "gameService must not be null");
        this.gamePresenter = Objects.requireNonNull(gamePresenter, "gamePresenter must not be null");
    }

    public GameViewModel startGame(List<String> playerNames) {
        currentGameId = gameService.startGame(playerNames);
        return showCurrentGame();
    }

    public GameViewModel advancePhase() {
        ensureGameStarted();
        Game game = gameService.advancePhase(currentGameId);
        return gamePresenter.present(game);
    }

    public GameViewModel placeBet(String playerName, int amount) {
        ensureGameStarted();
        Game current = gameService.getGame(currentGameId);
        PlayerId playerId = resolvePlayerId(current, playerName);
        Game game = gameService.placeBet(currentGameId, playerId, new Chips(amount));
        return gamePresenter.present(game);
    }

    public GameViewModel fold(String playerName) {
        ensureGameStarted();
        Game current = gameService.getGame(currentGameId);
        PlayerId playerId = resolvePlayerId(current, playerName);
        Game game = gameService.fold(currentGameId, playerId);
        return gamePresenter.present(game);
    }

    public GameViewModel showCurrentGame() {
        ensureGameStarted();
        Game game = gameService.getGame(currentGameId);
        return gamePresenter.present(game);
    }

    public boolean hasCurrentGame() {
        return currentGameId != null;
    }

    private PlayerId resolvePlayerId(Game game, String playerName) {
        for (Player player : game.players()) {
            if (player.name().equalsIgnoreCase(playerName)) {
                return player.id();
            }
        }
        throw new IllegalArgumentException("unknown player: " + playerName);
    }

    private void ensureGameStarted() {
        if (currentGameId == null) {
            throw new IllegalStateException("no game has been started yet");
        }
    }
}