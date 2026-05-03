package de.pokersim.domain;

import java.util.Objects;

public final class PlayerAction {
    private final int sequenceNumber;
    private final PlayerId playerId;
    private final PlayerActionType type;
    private final Chips amount;
    private final GamePhase phase;

    public PlayerAction(
            int sequenceNumber,
            PlayerId playerId,
            PlayerActionType type,
            Chips amount,
            GamePhase phase
    ) {
        if (sequenceNumber < 1) {
            throw new IllegalArgumentException("sequence number must be positive");
        }

        this.sequenceNumber = sequenceNumber;
        this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.phase = Objects.requireNonNull(phase, "phase must not be null");
    }

    public int sequenceNumber() {
        return sequenceNumber;
    }

    public PlayerId playerId() {
        return playerId;
    }

    public PlayerActionType type() {
        return type;
    }

    public Chips amount() {
        return amount;
    }

    public GamePhase phase() {
        return phase;
    }

    public boolean hasAmount() {
        return amount.amount() > 0;
    }

    public boolean wasPerformedDuring(GamePhase searchedPhase) {
        return phase == searchedPhase;
    }

    @Override
    public String toString() {
        if (hasAmount()) {
            return "#" + sequenceNumber + " " + playerId + " " + type + " " + amount + " during " + phase;
        }

        return "#" + sequenceNumber + " " + playerId + " " + type + " during " + phase;
    }
}

