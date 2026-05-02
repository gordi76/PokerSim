package de.pokersim.domain;

/**
 * Central definition of all game rules and constants for Texas Hold'em.
 *
 * Replaces magic numbers scattered across the codebase and serves as
 * the single source of truth for rule configuration.
 */
public final class GameRules {

    public static final int INITIAL_CHIPS = 1_000;
    public static final int SMALL_BLIND   = 10;
    public static final int MIN_PLAYERS   = 2;
    public static final int MAX_PLAYERS   = 9;

    private GameRules() {}
}
