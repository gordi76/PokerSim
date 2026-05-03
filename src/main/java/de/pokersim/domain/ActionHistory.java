package de.pokersim.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ActionHistory {
    private final List<PlayerAction> actions;

    public ActionHistory() {
        this.actions = new ArrayList<>();
    }

    public PlayerAction record(PlayerId playerId, PlayerActionType type, Chips amount, GamePhase phase) {
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(phase, "phase must not be null");

        PlayerAction action = new PlayerAction(nextSequenceNumber(), playerId, type, amount, phase);
        actions.add(action);
        return action;
    }

    public PlayerAction record(PlayerId playerId, PlayerActionType type, GamePhase phase) {
        return record(playerId, type, new Chips(0), phase);
    }

    public List<PlayerAction> actions() {
        return Collections.unmodifiableList(actions);
    }

    public List<PlayerAction> actionsOf(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId must not be null");

        List<PlayerAction> matchingActions = new ArrayList<>();

        for (PlayerAction action : actions) {
            if (action.playerId().equals(playerId)) {
                matchingActions.add(action);
            }
        }

        return Collections.unmodifiableList(matchingActions);
    }

    public List<PlayerAction> actionsDuring(GamePhase phase) {
        Objects.requireNonNull(phase, "phase must not be null");

        List<PlayerAction> matchingActions = new ArrayList<>();

        for (PlayerAction action : actions) {
            if (action.wasPerformedDuring(phase)) {
                matchingActions.add(action);
            }
        }

        return Collections.unmodifiableList(matchingActions);
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }

    public int size() {
        return actions.size();
    }

    public PlayerAction latestAction() {
        if (actions.isEmpty()) {
            throw new IllegalStateException("history does not contain any actions");
        }

        return actions.get(actions.size() - 1);
    }

    public void clear() {
        actions.clear();
    }

    private int nextSequenceNumber() {
        return actions.size() + 1;
    }
}