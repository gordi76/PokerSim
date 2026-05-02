package de.pokersim.adapters;

import java.util.List;

public record ShowdownResult(
        List<String> playerHandSummaries,
        String winnerName,
        int chipsWon
) {
    public ShowdownResult {
        playerHandSummaries = List.copyOf(playerHandSummaries);
    }
}
