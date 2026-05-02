package de.pokersim.domain;

import java.util.Set;

/**
 * Actions that the CLI layer can request during a given game phase.
 *
 * Each value knows which phases it is legal in, so the adapter layer
 * can validate commands before forwarding them to the application layer.
 */
public enum AllowedAction {

    BET,
    FOLD,
    ADVANCE_PHASE,
    SHOWDOWN;

    /** Returns the actions that are allowed in the given phase. */
    public static Set<AllowedAction> allowedIn(GamePhase phase) {
        return switch (phase) {
            case PRE_FLOP, FLOP, TURN, RIVER -> Set.of(BET, FOLD, ADVANCE_PHASE);
            case SHOWDOWN                    -> Set.of(SHOWDOWN);
            default                          -> Set.of();
        };
    }

    /** Returns true when this action may be executed in the given phase. */
    public boolean isAllowedIn(GamePhase phase) {
        return allowedIn(phase).contains(this);
    }
}
