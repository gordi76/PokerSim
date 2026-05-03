package de.pokersim.domain;

import java.util.List;
import java.util.Objects;

public final class RoundState {
    private final GamePhase phase;
    private final BettingRound bettingRound;
    private final Chips potTotal;
    private final List<PlayerAction> actions;

    public RoundState(
            GamePhase phase,
            BettingRound bettingRound,
            Chips potTotal,
            List<PlayerAction> actions
    ) {
        this.phase = Objects.requireNonNull(phase, "phase must not be null");
        this.bettingRound = Objects.requireNonNull(bettingRound, "bettingRound must not be null");
        this.potTotal = Objects.requireNonNull(potTotal, "potTotal must not be null");
        this.actions = List.copyOf(actions);
    }

    public GamePhase phase() {
        return phase;
    }

    public BettingRound bettingRound() {
        return bettingRound;
    }

    public Chips potTotal() {
        return potTotal;
    }

    public List<PlayerAction> actions() {
        return actions;
    }

    public int actionCount() {
        return actions.size();
    }

    public boolean hasActions() {
        return !actions.isEmpty();
    }

    @Override
    public String toString() {
        return "RoundState{"
                + "phase=" + phase
                + ", bettingRound=" + bettingRound
                + ", potTotal=" + potTotal
                + ", actions=" + actions.size()
                + '}';
    }
}