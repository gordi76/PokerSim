package de.pokersim.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer {@link AllowedAction#allowedIn(GamePhase)} und
 * {@link AllowedAction#isAllowedIn(GamePhase)}.
 *
 * <p>Prueft fuer jede {@link GamePhase}, welche Aktionen erlaubt
 * bzw. verboten sind, und sichert ab, dass beide Methoden konsistent
 * sind.</p>
 */
class PlayerActionTest {

    // =========================================================================
    // WAITING_FOR_PLAYERS — keine Aktion erlaubt
    // =========================================================================

    @Test
    @DisplayName("allowedIn(WAITING_FOR_PLAYERS) returns empty set")
    void waitingForPlayersAllowsNothing() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.WAITING_FOR_PLAYERS);
        assertTrue(allowed.isEmpty(),
                "no action should be allowed before the game starts");
    }

    @ParameterizedTest
    @EnumSource(AllowedAction.class)
    @DisplayName("isAllowedIn(WAITING_FOR_PLAYERS) is false for every action")
    void noActionAllowedInWaitingForPlayers(AllowedAction action) {
        assertFalse(action.isAllowedIn(GamePhase.WAITING_FOR_PLAYERS),
                action + " should not be allowed in WAITING_FOR_PLAYERS");
    }

    // =========================================================================
    // PRE_FLOP — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(PRE_FLOP) contains BET, FOLD, ADVANCE_PHASE")
    void preFlopAllowedSet() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.PRE_FLOP);
        assertEquals(Set.of(AllowedAction.BET, AllowedAction.FOLD, AllowedAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in PRE_FLOP")
    void betAllowedInPreFlop() {
        assertTrue(AllowedAction.BET.isAllowedIn(GamePhase.PRE_FLOP));
    }

    @Test
    @DisplayName("FOLD is allowed in PRE_FLOP")
    void foldAllowedInPreFlop() {
        assertTrue(AllowedAction.FOLD.isAllowedIn(GamePhase.PRE_FLOP));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in PRE_FLOP")
    void advancePhaseAllowedInPreFlop() {
        assertTrue(AllowedAction.ADVANCE_PHASE.isAllowedIn(GamePhase.PRE_FLOP));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in PRE_FLOP")
    void showdownNotAllowedInPreFlop() {
        assertFalse(AllowedAction.SHOWDOWN.isAllowedIn(GamePhase.PRE_FLOP));
    }

    // =========================================================================
    // FLOP — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(FLOP) contains BET, FOLD, ADVANCE_PHASE")
    void flopAllowedSet() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.FLOP);
        assertEquals(Set.of(AllowedAction.BET, AllowedAction.FOLD, AllowedAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in FLOP")
    void betAllowedInFlop() {
        assertTrue(AllowedAction.BET.isAllowedIn(GamePhase.FLOP));
    }

    @Test
    @DisplayName("FOLD is allowed in FLOP")
    void foldAllowedInFlop() {
        assertTrue(AllowedAction.FOLD.isAllowedIn(GamePhase.FLOP));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in FLOP")
    void advancePhaseAllowedInFlop() {
        assertTrue(AllowedAction.ADVANCE_PHASE.isAllowedIn(GamePhase.FLOP));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in FLOP")
    void showdownNotAllowedInFlop() {
        assertFalse(AllowedAction.SHOWDOWN.isAllowedIn(GamePhase.FLOP));
    }

    // =========================================================================
    // TURN — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(TURN) contains BET, FOLD, ADVANCE_PHASE")
    void turnAllowedSet() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.TURN);
        assertEquals(Set.of(AllowedAction.BET, AllowedAction.FOLD, AllowedAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in TURN")
    void betAllowedInTurn() {
        assertTrue(AllowedAction.BET.isAllowedIn(GamePhase.TURN));
    }

    @Test
    @DisplayName("FOLD is allowed in TURN")
    void foldAllowedInTurn() {
        assertTrue(AllowedAction.FOLD.isAllowedIn(GamePhase.TURN));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in TURN")
    void advancePhaseAllowedInTurn() {
        assertTrue(AllowedAction.ADVANCE_PHASE.isAllowedIn(GamePhase.TURN));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in TURN")
    void showdownNotAllowedInTurn() {
        assertFalse(AllowedAction.SHOWDOWN.isAllowedIn(GamePhase.TURN));
    }

    // =========================================================================
    // RIVER — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(RIVER) contains BET, FOLD, ADVANCE_PHASE")
    void riverAllowedSet() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.RIVER);
        assertEquals(Set.of(AllowedAction.BET, AllowedAction.FOLD, AllowedAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in RIVER")
    void betAllowedInRiver() {
        assertTrue(AllowedAction.BET.isAllowedIn(GamePhase.RIVER));
    }

    @Test
    @DisplayName("FOLD is allowed in RIVER")
    void foldAllowedInRiver() {
        assertTrue(AllowedAction.FOLD.isAllowedIn(GamePhase.RIVER));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in RIVER")
    void advancePhaseAllowedInRiver() {
        assertTrue(AllowedAction.ADVANCE_PHASE.isAllowedIn(GamePhase.RIVER));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in RIVER")
    void showdownNotAllowedInRiver() {
        assertFalse(AllowedAction.SHOWDOWN.isAllowedIn(GamePhase.RIVER));
    }

    // =========================================================================
    // SHOWDOWN — only SHOWDOWN allowed
    // =========================================================================

    @Test
    @DisplayName("allowedIn(SHOWDOWN) contains only SHOWDOWN")
    void showdownAllowedSet() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.SHOWDOWN);
        assertEquals(Set.of(AllowedAction.SHOWDOWN), allowed);
    }

    @Test
    @DisplayName("SHOWDOWN is allowed in SHOWDOWN phase")
    void showdownAllowedInShowdown() {
        assertTrue(AllowedAction.SHOWDOWN.isAllowedIn(GamePhase.SHOWDOWN));
    }

    @Test
    @DisplayName("BET is NOT allowed in SHOWDOWN")
    void betNotAllowedInShowdown() {
        assertFalse(AllowedAction.BET.isAllowedIn(GamePhase.SHOWDOWN));
    }

    @Test
    @DisplayName("FOLD is NOT allowed in SHOWDOWN")
    void foldNotAllowedInShowdown() {
        assertFalse(AllowedAction.FOLD.isAllowedIn(GamePhase.SHOWDOWN));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is NOT allowed in SHOWDOWN")
    void advancePhaseNotAllowedInShowdown() {
        assertFalse(AllowedAction.ADVANCE_PHASE.isAllowedIn(GamePhase.SHOWDOWN));
    }

    // =========================================================================
    // FINISHED — keine Aktion erlaubt
    // =========================================================================

    @Test
    @DisplayName("allowedIn(FINISHED) returns empty set")
    void finishedAllowsNothing() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.FINISHED);
        assertTrue(allowed.isEmpty(),
                "no action should be allowed after the game is finished");
    }

    @ParameterizedTest
    @EnumSource(AllowedAction.class)
    @DisplayName("isAllowedIn(FINISHED) is false for every action")
    void noActionAllowedInFinished(AllowedAction action) {
        assertFalse(action.isAllowedIn(GamePhase.FINISHED),
                action + " should not be allowed in FINISHED");
    }

    // =========================================================================
    // Konsistenz: allowedIn() und isAllowedIn() sind aequivalent
    // =========================================================================

    @ParameterizedTest
    @EnumSource(GamePhase.class)
    @DisplayName("isAllowedIn is consistent with allowedIn for every phase")
    void isAllowedInConsistentWithAllowedIn(GamePhase phase) {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(phase);
        for (AllowedAction action : AllowedAction.values()) {
            assertEquals(allowed.contains(action), action.isAllowedIn(phase),
                    action + ".isAllowedIn(" + phase + ") should match allowedIn(" + phase + ").contains(" + action + ")");
        }
    }

    // =========================================================================
    // Phasen-Symmetrie: PRE_FLOP, FLOP, TURN, RIVER haben identische Aktionen
    // =========================================================================

    @Test
    @DisplayName("PRE_FLOP, FLOP, TURN and RIVER all allow the same actions")
    void bettingPhasesHaveSameAllowedActions() {
        Set<AllowedAction> preFlopAllowed = AllowedAction.allowedIn(GamePhase.PRE_FLOP);
        assertEquals(preFlopAllowed, AllowedAction.allowedIn(GamePhase.FLOP),
                "FLOP should allow same actions as PRE_FLOP");
        assertEquals(preFlopAllowed, AllowedAction.allowedIn(GamePhase.TURN),
                "TURN should allow same actions as PRE_FLOP");
        assertEquals(preFlopAllowed, AllowedAction.allowedIn(GamePhase.RIVER),
                "RIVER should allow same actions as PRE_FLOP");
    }

    @Test
    @DisplayName("allowedIn never returns null")
    void allowedInNeverReturnsNull() {
        for (GamePhase phase : GamePhase.values()) {
            assertNotNull(AllowedAction.allowedIn(phase),
                    "allowedIn(" + phase + ") must not return null");
        }
    }

    @Test
    @DisplayName("allowedIn returns unmodifiable or independent set")
    void allowedInReturnsDefensiveCopy() {
        Set<AllowedAction> allowed = AllowedAction.allowedIn(GamePhase.PRE_FLOP);
        // Set.of() returns unmodifiable — verify mutation is rejected
        assertThrows(UnsupportedOperationException.class,
                () -> allowed.add(AllowedAction.SHOWDOWN));
    }
}
