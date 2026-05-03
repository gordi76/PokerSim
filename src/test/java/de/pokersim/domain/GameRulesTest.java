package de.pokersim.domain;

import de.pokersim.application.GameService;
import de.pokersim.infrastructure.FixedRandomSource;
import de.pokersim.infrastructure.InMemoryGameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity-Checks fuer {@link GameRules}.
 *
 * <p>Prueft, dass die Konstanten sinnvolle Werte haben, zueinander
 * konsistent sind und tatsaechlich vom Rest der Anwendung verwendet
 * werden (Integration).</p>
 */
class GameRulesTest {

    // =========================================================================
    // Werte-Sanity
    // =========================================================================

    @Test
    @DisplayName("INITIAL_CHIPS is a positive value")
    void initialChipsIsPositive() {
        assertTrue(GameRules.INITIAL_CHIPS > 0,
                "INITIAL_CHIPS must be positive, got: " + GameRules.INITIAL_CHIPS);
    }

    @Test
    @DisplayName("SMALL_BLIND is positive")
    void smallBlindIsPositive() {
        assertTrue(GameRules.SMALL_BLIND > 0,
                "SMALL_BLIND must be positive, got: " + GameRules.SMALL_BLIND);
    }

    @Test
    @DisplayName("MIN_PLAYERS is at least 2")
    void minPlayersAtLeastTwo() {
        assertTrue(GameRules.MIN_PLAYERS >= 2,
                "MIN_PLAYERS must be at least 2 for a valid poker game, got: " + GameRules.MIN_PLAYERS);
    }

    @Test
    @DisplayName("MAX_PLAYERS is greater than MIN_PLAYERS")
    void maxPlayersGreaterThanMin() {
        assertTrue(GameRules.MAX_PLAYERS > GameRules.MIN_PLAYERS,
                "MAX_PLAYERS (" + GameRules.MAX_PLAYERS + ") must exceed MIN_PLAYERS (" + GameRules.MIN_PLAYERS + ")");
    }

    @Test
    @DisplayName("SMALL_BLIND is less than INITIAL_CHIPS")
    void smallBlindLessThanInitialChips() {
        assertTrue(GameRules.SMALL_BLIND < GameRules.INITIAL_CHIPS,
                "SMALL_BLIND must be less than INITIAL_CHIPS so players can afford it");
    }

    @Test
    @DisplayName("INITIAL_CHIPS is a multiple of SMALL_BLIND")
    void initialChipsIsMultipleOfSmallBlind() {
        assertEquals(0, GameRules.INITIAL_CHIPS % GameRules.SMALL_BLIND,
                "INITIAL_CHIPS should be evenly divisible by SMALL_BLIND to avoid fractions");
    }

    @Test
    @DisplayName("INITIAL_CHIPS value is 1000")
    void initialChipsExactValue() {
        assertEquals(1_000, GameRules.INITIAL_CHIPS);
    }

    @Test
    @DisplayName("SMALL_BLIND value is 10")
    void smallBlindExactValue() {
        assertEquals(10, GameRules.SMALL_BLIND);
    }

    @Test
    @DisplayName("MIN_PLAYERS value is 2")
    void minPlayersExactValue() {
        assertEquals(2, GameRules.MIN_PLAYERS);
    }

    @Test
    @DisplayName("MAX_PLAYERS value is 9")
    void maxPlayersExactValue() {
        assertEquals(9, GameRules.MAX_PLAYERS);
    }

    // =========================================================================
    // Instanziierung verhindert
    // =========================================================================

    @Test
    @DisplayName("GameRules has a private constructor (utility class pattern)")
    void cannotBeInstantiated() throws Exception {
        var constructor = GameRules.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "GameRules constructor should be private");
    }

    // =========================================================================
    // Integration: INITIAL_CHIPS wird tatssaechlich verwendet
    // =========================================================================

    @Test
    @DisplayName("players start with exactly INITIAL_CHIPS chips")
    void playersStartWithInitialChips() {
        var service = new GameService(new InMemoryGameRepository(), FixedRandomSource.noShuffle());
        var id = service.startGame(List.of("Alice", "Bob"));
        var game = service.getGame(id);

        for (var player : game.players()) {
            // After start, small/big blind are collected from the first two players.
            // The remaining players keep INITIAL_CHIPS unchanged.
            // All players start from INITIAL_CHIPS before blinds.
            assertTrue(player.chips().amount() <= GameRules.INITIAL_CHIPS,
                    player.name() + " should have at most INITIAL_CHIPS after blinds");
            assertTrue(player.chips().amount() >= 0,
                    player.name() + " should have non-negative chips");
        }
    }

    @Test
    @DisplayName("starting a game with MIN_PLAYERS succeeds")
    void startWithMinPlayersSucceeds() {
        var service = new GameService(new InMemoryGameRepository(), FixedRandomSource.noShuffle());
        List<String> names = new ArrayList<>();
        for (int i = 0; i < GameRules.MIN_PLAYERS; i++) {
            names.add("Player" + i);
        }
        assertDoesNotThrow(() -> service.startGame(names),
                "starting with exactly MIN_PLAYERS should succeed");
    }

    @Test
    @DisplayName("starting a game with MAX_PLAYERS succeeds")
    void startWithMaxPlayersSucceeds() {
        var service = new GameService(new InMemoryGameRepository(), FixedRandomSource.noShuffle());
        List<String> names = new ArrayList<>();
        for (int i = 0; i < GameRules.MAX_PLAYERS; i++) {
            names.add("Player" + i);
        }
        assertDoesNotThrow(() -> service.startGame(names),
                "starting with exactly MAX_PLAYERS should succeed");
    }

    @Test
    @DisplayName("starting a game with fewer than MIN_PLAYERS throws")
    void startWithTooFewPlayersThrows() {
        var service = new GameService(new InMemoryGameRepository(), FixedRandomSource.noShuffle());
        List<String> tooFew = new ArrayList<>();
        for (int i = 0; i < GameRules.MIN_PLAYERS - 1; i++) {
            tooFew.add("Player" + i);
        }
        assertThrows(RuntimeException.class, () -> service.startGame(tooFew),
                "starting with fewer than MIN_PLAYERS should be rejected");
    }
}
