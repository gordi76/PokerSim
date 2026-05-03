package de.pokersim.adapters;

import java.util.List;

/**
 * Summary of a finished poker game, produced after the showdown.
 *
 * Captures who won, how much, each player's final hand, and how many
 * players folded before the showdown.
 */
public record GameSummary(
        List<String> playerHandSummaries,
        String winnerName,
        int chipsWon,
        int totalPlayers,
        int foldedPlayers
) {
    public GameSummary {
        playerHandSummaries = List.copyOf(playerHandSummaries);
    }
}
