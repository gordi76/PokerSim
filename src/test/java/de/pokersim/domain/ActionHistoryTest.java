package de.pokersim.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionHistoryTest {

    @Test
    void startsEmpty() {
        ActionHistory history = new ActionHistory();

        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
    }

    @Test
    void recordsActionWithSequenceNumber() {
        ActionHistory history = new ActionHistory();
        PlayerId playerId = PlayerId.of("player-1");

        PlayerAction action = history.record(
                playerId,
                PlayerActionType.BET,
                new Chips(50),
                GamePhase.PRE_FLOP
        );

        assertEquals(1, action.sequenceNumber());
        assertEquals(playerId, action.playerId());
        assertEquals(PlayerActionType.BET, action.type());
        assertEquals(50, action.amount().amount());
        assertEquals(GamePhase.PRE_FLOP, action.phase());
        assertEquals(1, history.size());
    }

    @Test
    void increasesSequenceNumberForEveryRecordedAction() {
        ActionHistory history = new ActionHistory();
        PlayerId playerId = PlayerId.of("player-1");

        PlayerAction firstAction = history.record(playerId, PlayerActionType.CHECK, GamePhase.FLOP);
        PlayerAction secondAction = history.record(playerId, PlayerActionType.FOLD, GamePhase.TURN);

        assertEquals(1, firstAction.sequenceNumber());
        assertEquals(2, secondAction.sequenceNumber());
    }

    @Test
    void returnsLatestAction() {
        ActionHistory history = new ActionHistory();
        PlayerId playerId = PlayerId.of("player-1");

        history.record(playerId, PlayerActionType.CHECK, GamePhase.FLOP);
        PlayerAction latest = history.record(playerId, PlayerActionType.FOLD, GamePhase.TURN);

        assertEquals(latest, history.latestAction());
    }

    @Test
    void latestActionFailsWhenHistoryIsEmpty() {
        ActionHistory history = new ActionHistory();

        assertThrows(IllegalStateException.class, history::latestAction);
    }

    @Test
    void filtersActionsByPlayer() {
        ActionHistory history = new ActionHistory();
        PlayerId alice = PlayerId.of("alice");
        PlayerId bob = PlayerId.of("bob");

        history.record(alice, PlayerActionType.BET, new Chips(25), GamePhase.PRE_FLOP);
        history.record(bob, PlayerActionType.FOLD, GamePhase.PRE_FLOP);
        history.record(alice, PlayerActionType.CHECK, GamePhase.FLOP);

        List<PlayerAction> aliceActions = history.actionsOf(alice);

        assertEquals(2, aliceActions.size());
        assertEquals(alice, aliceActions.get(0).playerId());
        assertEquals(alice, aliceActions.get(1).playerId());
    }

    @Test
    void filtersActionsByPhase() {
        ActionHistory history = new ActionHistory();
        PlayerId playerId = PlayerId.of("player-1");

        history.record(playerId, PlayerActionType.BET, new Chips(25), GamePhase.PRE_FLOP);
        history.record(playerId, PlayerActionType.CHECK, GamePhase.FLOP);
        history.record(playerId, PlayerActionType.FOLD, GamePhase.FLOP);

        List<PlayerAction> flopActions = history.actionsDuring(GamePhase.FLOP);

        assertEquals(2, flopActions.size());
        assertTrue(flopActions.stream().allMatch(action -> action.phase() == GamePhase.FLOP));
    }

    @Test
    void clearRemovesAllActions() {
        ActionHistory history = new ActionHistory();
        PlayerId playerId = PlayerId.of("player-1");

        history.record(playerId, PlayerActionType.BET, new Chips(10), GamePhase.PRE_FLOP);
        history.clear();

        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
    }

    @Test
    void returnedActionListCannotBeModified() {
        ActionHistory history = new ActionHistory();
        PlayerId playerId = PlayerId.of("player-1");

        history.record(playerId, PlayerActionType.CHECK, GamePhase.FLOP);

        assertThrows(UnsupportedOperationException.class, () ->
                history.actions().add(new PlayerAction(
                        2,
                        playerId,
                        PlayerActionType.FOLD,
                        new Chips(0),
                        GamePhase.FLOP
                ))
        );
    }
}