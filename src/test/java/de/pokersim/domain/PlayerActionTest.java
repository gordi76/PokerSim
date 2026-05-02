package de.pokersim.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer {@link PlayerAction#allowedIn(GamePhase)} und
 * {@link PlayerAction#isAllowedIn(GamePhase)}.
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
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.WAITING_FOR_PLAYERS);
        assertTrue(allowed.isEmpty(),
                "no action should be allowed before the game starts");
    }

    @ParameterizedTest
    @EnumSource(PlayerAction.class)
    @DisplayName("isAllowedIn(WAITING_FOR_PLAYERS) is false for every action")
    void noActionAllowedInWaitingForPlayers(PlayerAction action) {
        assertFalse(action.isAllowedIn(GamePhase.WAITING_FOR_PLAYERS),
                action + " should not be allowed in WAITING_FOR_PLAYERS");
    }

    // =========================================================================
    // PRE_FLOP — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(PRE_FLOP) contains BET, FOLD, ADVANCE_PHASE")
    void preFlopAllowedSet() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.PRE_FLOP);
        assertEquals(Set.of(PlayerAction.BET, PlayerAction.FOLD, PlayerAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in PRE_FLOP")
    void betAllowedInPreFlop() {
        assertTrue(PlayerAction.BET.isAllowedIn(GamePhase.PRE_FLOP));
    }

    @Test
    @DisplayName("FOLD is allowed in PRE_FLOP")
    void foldAllowedInPreFlop() {
        assertTrue(PlayerAction.FOLD.isAllowedIn(GamePhase.PRE_FLOP));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in PRE_FLOP")
    void advancePhaseAllowedInPreFlop() {
        assertTrue(PlayerAction.ADVANCE_PHASE.isAllowedIn(GamePhase.PRE_FLOP));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in PRE_FLOP")
    void showdownNotAllowedInPreFlop() {
        assertFalse(PlayerAction.SHOWDOWN.isAllowedIn(GamePhase.PRE_FLOP));
    }

    // =========================================================================
    // FLOP — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(FLOP) contains BET, FOLD, ADVANCE_PHASE")
    void flopAllowedSet() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.FLOP);
        assertEquals(Set.of(PlayerAction.BET, PlayerAction.FOLD, PlayerAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in FLOP")
    void betAllowedInFlop() {
        assertTrue(PlayerAction.BET.isAllowedIn(GamePhase.FLOP));
    }

    @Test
    @DisplayName("FOLD is allowed in FLOP")
    void foldAllowedInFlop() {
        assertTrue(PlayerAction.FOLD.isAllowedIn(GamePhase.FLOP));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in FLOP")
    void advancePhaseAllowedInFlop() {
        assertTrue(PlayerAction.ADVANCE_PHASE.isAllowedIn(GamePhase.FLOP));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in FLOP")
    void showdownNotAllowedInFlop() {
        assertFalse(PlayerAction.SHOWDOWN.isAllowedIn(GamePhase.FLOP));
    }

    // =========================================================================
    // TURN — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(TURN) contains BET, FOLD, ADVANCE_PHASE")
    void turnAllowedSet() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.TURN);
        assertEquals(Set.of(PlayerAction.BET, PlayerAction.FOLD, PlayerAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in TURN")
    void betAllowedInTurn() {
        assertTrue(PlayerAction.BET.isAllowedIn(GamePhase.TURN));
    }

    @Test
    @DisplayName("FOLD is allowed in TURN")
    void foldAllowedInTurn() {
        assertTrue(PlayerAction.FOLD.isAllowedIn(GamePhase.TURN));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in TURN")
    void advancePhaseAllowedInTurn() {
        assertTrue(PlayerAction.ADVANCE_PHASE.isAllowedIn(GamePhase.TURN));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in TURN")
    void showdownNotAllowedInTurn() {
        assertFalse(PlayerAction.SHOWDOWN.isAllowedIn(GamePhase.TURN));
    }

    // =========================================================================
    // RIVER — BET, FOLD, ADVANCE_PHASE
    // =========================================================================

    @Test
    @DisplayName("allowedIn(RIVER) contains BET, FOLD, ADVANCE_PHASE")
    void riverAllowedSet() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.RIVER);
        assertEquals(Set.of(PlayerAction.BET, PlayerAction.FOLD, PlayerAction.ADVANCE_PHASE),
                allowed);
    }

    @Test
    @DisplayName("BET is allowed in RIVER")
    void betAllowedInRiver() {
        assertTrue(PlayerAction.BET.isAllowedIn(GamePhase.RIVER));
    }

    @Test
    @DisplayName("FOLD is allowed in RIVER")
    void foldAllowedInRiver() {
        assertTrue(PlayerAction.FOLD.isAllowedIn(GamePhase.RIVER));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is allowed in RIVER")
    void advancePhaseAllowedInRiver() {
        assertTrue(PlayerAction.ADVANCE_PHASE.isAllowedIn(GamePhase.RIVER));
    }

    @Test
    @DisplayName("SHOWDOWN is NOT allowed in RIVER")
    void showdownNotAllowedInRiver() {
        assertFalse(PlayerAction.SHOWDOWN.isAllowedIn(GamePhase.RIVER));
    }

    // =========================================================================
    // SHOWDOWN — only SHOWDOWN allowed
    // =========================================================================

    @Test
    @DisplayName("allowedIn(SHOWDOWN) contains only SHOWDOWN")
    void showdownAllowedSet() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.SHOWDOWN);
        assertEquals(Set.of(PlayerAction.SHOWDOWN), allowed);
    }

    @Test
    @DisplayName("SHOWDOWN is allowed in SHOWDOWN phase")
    void showdownAllowedInShowdown() {
        assertTrue(PlayerAction.SHOWDOWN.isAllowedIn(GamePhase.SHOWDOWN));
    }

    @Test
    @DisplayName("BET is NOT allowed in SHOWDOWN")
    void betNotAllowedInShowdown() {
        assertFalse(PlayerAction.BET.isAllowedIn(GamePhase.SHOWDOWN));
    }

    @Test
    @DisplayName("FOLD is NOT allowed in SHOWDOWN")
    void foldNotAllowedInShowdown() {
        assertFalse(PlayerAction.FOLD.isAllowedIn(GamePhase.SHOWDOWN));
    }

    @Test
    @DisplayName("ADVANCE_PHASE is NOT allowed in SHOWDOWN")
    void advancePhaseNotAllowedInShowdown() {
        assertFalse(PlayerAction.ADVANCE_PHASE.isAllowedIn(GamePhase.SHOWDOWN));
    }

    // =========================================================================
    // FINISHED — keine Aktion erlaubt
    // =========================================================================

    @Test
    @DisplayName("allowedIn(FINISHED) returns empty set")
    void finishedAllowsNothing() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.FINISHED);
        assertTrue(allowed.isEmpty(),
                "no action should be allowed after the game is finished");
    }

    @ParameterizedTest
    @EnumSource(PlayerAction.class)
    @DisplayName("isAllowedIn(FINISHED) is false for every action")
    void noActionAllowedInFinished(PlayerAction action) {
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
        Set<PlayerAction> allowed = PlayerAction.allowedIn(phase);
        for (PlayerAction action : PlayerAction.values()) {
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
        Set<PlayerAction> preFlopAllowed = PlayerAction.allowedIn(GamePhase.PRE_FLOP);
        assertEquals(preFlopAllowed, PlayerAction.allowedIn(GamePhase.FLOP),
                "FLOP should allow same actions as PRE_FLOP");
        assertEquals(preFlopAllowed, PlayerAction.allowedIn(GamePhase.TURN),
                "TURN should allow same actions as PRE_FLOP");
        assertEquals(preFlopAllowed, PlayerAction.allowedIn(GamePhase.RIVER),
                "RIVER should allow same actions as PRE_FLOP");
    }

    @Test
    @DisplayName("allowedIn never returns null")
    void allowedInNeverReturnsNull() {
        for (GamePhase phase : GamePhase.values()) {
            assertNotNull(PlayerAction.allowedIn(phase),
                    "allowedIn(" + phase + ") must not return null");
        }
    }

    @Test
    @DisplayName("allowedIn returns unmodifiable or independent set")
    void allowedInReturnsDefensiveCopy() {
        Set<PlayerAction> allowed = PlayerAction.allowedIn(GamePhase.PRE_FLOP);
        // Set.of() returns unmodifiable — verify mutation is rejected
        assertThrows(UnsupportedOperationException.class,
                () -> allowed.add(PlayerAction.SHOWDOWN));
    }
}
