package de.pokersim.domain;

import java.util.Objects;

public final class BettingRules {
    private static final int MINIMUM_BET = 1;

    public void validateBet(Player player, Chips amount, GamePhase phase) {
        Objects.requireNonNull(player, "player must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(phase, "phase must not be null");

        ensureGameAcceptsPlayerActions(phase);
        ensurePlayerIsActive(player);
        ensureBetHasPositiveAmount(amount);
        ensurePlayerCanPay(player, amount);
    }

    public void validateFold(Player player, GamePhase phase) {
        Objects.requireNonNull(player, "player must not be null");
        Objects.requireNonNull(phase, "phase must not be null");

        ensureGameAcceptsPlayerActions(phase);
        ensurePlayerIsActive(player);
    }

    private void ensureGameAcceptsPlayerActions(GamePhase phase) {
        if (phase == GamePhase.WAITING_FOR_PLAYERS) {
            throw new InvalidPlayerActionException("players cannot act before the game has started");
        }

        if (phase == GamePhase.SHOWDOWN || phase == GamePhase.FINISHED) {
            throw new InvalidPlayerActionException("players cannot act after betting has ended");
        }
    }

    private void ensurePlayerIsActive(Player player) {
        if (player.hasFolded()) {
            throw new InvalidPlayerActionException("folded players cannot perform actions");
        }
    }

    private void ensureBetHasPositiveAmount(Chips amount) {
        if (amount.amount() < MINIMUM_BET) {
            throw new InvalidPlayerActionException("bet must be at least " + MINIMUM_BET + " chip");
        }
    }

    private void ensurePlayerCanPay(Player player, Chips amount) {
        if (player.chips().compareTo(amount) < 0) {
            throw new InvalidPlayerActionException("player does not have enough chips");
        }
    }
}