package de.pokersim.adapters;

import de.pokersim.application.GameService;
import de.pokersim.domain.Card;
import de.pokersim.domain.Chips;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameCommand;
import de.pokersim.domain.GameId;
import de.pokersim.domain.GamePhase;
import de.pokersim.domain.HandRank;
import de.pokersim.domain.Player;
import de.pokersim.domain.AllowedAction;
import de.pokersim.domain.PlayerId;
import de.pokersim.domain.TexasHoldemHandEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameController {

    private final GameService gameService;
    private final GamePresenter gamePresenter;
    private GameId currentGameId;
    private final List<GameCommand> commandHistory = new ArrayList<>();

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
        ensureActionAllowed(AllowedAction.ADVANCE_PHASE);
        Game game = gameService.advancePhase(currentGameId);
        commandHistory.add(new GameCommand.AdvancePhaseCommand());
        return gamePresenter.present(game);
    }

    public GameViewModel placeBet(String playerName, int amount) {
        ensureGameStarted();
        ensureActionAllowed(AllowedAction.BET);
        Game current = gameService.getGame(currentGameId);
        PlayerId playerId = resolvePlayerId(current, playerName);
        Game game = gameService.placeBet(currentGameId, playerId, new Chips(amount));
        commandHistory.add(new GameCommand.BetCommand(playerName, amount));
        return gamePresenter.present(game);
    }

    public GameViewModel fold(String playerName) {
        ensureGameStarted();
        ensureActionAllowed(AllowedAction.FOLD);
        Game current = gameService.getGame(currentGameId);
        PlayerId playerId = resolvePlayerId(current, playerName);
        Game game = gameService.fold(currentGameId, playerId);
        commandHistory.add(new GameCommand.FoldCommand(playerName));
        return gamePresenter.present(game);
    }

    public GameViewModel showCurrentGame() {
        ensureGameStarted();
        Game game = gameService.getGame(currentGameId);
        return gamePresenter.present(game);
    }

    public String getPlayerHand(String playerName) {
        ensureGameStarted();
        Game game = gameService.getGame(currentGameId);
        PlayerId playerId = resolvePlayerId(game, playerName);
        Player player = null;
        for (Player p : game.players()) {
            if (p.id().equals(playerId)) {
                player = p;
                break;
            }
        }
        if (player.hasFolded()) {
            return playerName + " has folded.";
        }
        String cards = String.join(" ", gamePresenter.formatCards(player.holeCards()));
        return playerName + "'s hand: " + cards;
    }

    public GameSummary runShowdown() {
        ensureGameStarted();
        Game game = gameService.getGame(currentGameId);

        if (game.phase() == GamePhase.WAITING_FOR_PLAYERS || game.phase() == GamePhase.FINISHED) {
            throw new IllegalStateException("cannot run showdown in phase " + game.phase());
        }

        // Advance to SHOWDOWN phase
        while (game.phase() != GamePhase.SHOWDOWN) {
            game = gameService.advancePhase(currentGameId);
        }

        // Evaluate hand ranks for all players before payout
        TexasHoldemHandEvaluator evaluator = new TexasHoldemHandEvaluator();
        List<String> summaries = new ArrayList<>();
        int foldedCount = 0;
        for (Player player : game.players()) {
            if (player.hasFolded()) {
                summaries.add(player.name() + ": [folded]");
                foldedCount++;
            } else {
                List<Card> allCards = new ArrayList<>(player.holeCards());
                allCards.addAll(game.communityCards());
                HandRank rank = evaluator.evaluate(allCards);
                String cards = String.join(", ", gamePresenter.formatCards(player.holeCards()));
                summaries.add(player.name() + ": " + cards + " -> " + rank.name());
            }
        }

        int potAmount = game.pot().total().amount();
        Player winner = gameService.showdown(currentGameId);
        commandHistory.add(new GameCommand.ShowdownCommand());

        return new GameSummary(summaries, winner.name(), potAmount, game.players().size(), foldedCount);
    }

    public List<GameCommand> commandHistory() {
        return Collections.unmodifiableList(commandHistory);
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
        List<String> names = new ArrayList<>();
        for (Player player : game.players()) {
            names.add(player.name());
        }
        throw new IllegalArgumentException(
                "unknown player '" + playerName + "'. Available players: " + String.join(", ", names));
    }

    private void ensureActionAllowed(AllowedAction action) {
        Game game = gameService.getGame(currentGameId);
        if (!action.isAllowedIn(game.phase())) {
            throw new IllegalStateException(
                    "'" + action.name().toLowerCase().replace('_', ' ')
                    + "' is not allowed in phase " + game.phase()
                    + ". Allowed actions: " + AllowedAction.allowedIn(game.phase()));
        }
    }

    private void ensureGameStarted() {
        if (currentGameId == null) {
            throw new IllegalStateException("no game started yet. Use: start Alice Bob");
        }
    }
}