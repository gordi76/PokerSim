package de.pokersim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BettingRulesTest {

    @Test
    void acceptsValidBetDuringPreFlop() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertDoesNotThrow(() -> rules.validateBet(player, new Chips(25), GamePhase.PRE_FLOP));
    }

    @Test
    void rejectsBetBeforeGameStarted() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateBet(player, new Chips(25), GamePhase.WAITING_FOR_PLAYERS)
        );
    }

    @Test
    void rejectsBetDuringShowdown() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateBet(player, new Chips(25), GamePhase.SHOWDOWN)
        );
    }

    @Test
    void rejectsBetAfterGameFinished() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateBet(player, new Chips(25), GamePhase.FINISHED)
        );
    }

    @Test
    void rejectsZeroBet() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateBet(player, new Chips(0), GamePhase.PRE_FLOP)
        );
    }

    @Test
    void rejectsBetWhenPlayerHasNotEnoughChips() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(20));

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateBet(player, new Chips(50), GamePhase.PRE_FLOP)
        );
    }

    @Test
    void rejectsBetFromFoldedPlayer() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));
        player.fold();

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateBet(player, new Chips(10), GamePhase.FLOP)
        );
    }

    @Test
    void acceptsValidFoldDuringFlop() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertDoesNotThrow(() -> rules.validateFold(player, GamePhase.FLOP));
    }

    @Test
    void rejectsFoldBeforeGameStarted() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateFold(player, GamePhase.WAITING_FOR_PLAYERS)
        );
    }

    @Test
    void rejectsFoldFromAlreadyFoldedPlayer() {
        BettingRules rules = new BettingRules();
        Player player = new Player(PlayerId.of("player-1"), "Alice", new Chips(100));
        player.fold();

        assertThrows(
                InvalidPlayerActionException.class,
                () -> rules.validateFold(player, GamePhase.TURN)
        );
    }
}