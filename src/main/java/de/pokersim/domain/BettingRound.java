package de.pokersim.domain;

public enum BettingRound {
    PRE_FLOP,
    FLOP,
    TURN,
    RIVER,
    SHOWDOWN;

    public static BettingRound from(GamePhase phase) {
        return switch (phase) {
            case PRE_FLOP -> PRE_FLOP;
            case FLOP -> FLOP;
            case TURN -> TURN;
            case RIVER -> RIVER;
            case SHOWDOWN, FINISHED -> SHOWDOWN;
            case WAITING_FOR_PLAYERS -> throw new InvalidPlayerActionException(
                    "there is no betting round before the game has started"
            );
        };
    }

    public boolean allowsPlayerActions() {
        return this != SHOWDOWN;
    }
}