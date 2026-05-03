package de.pokersim.adapters;

import de.pokersim.application.GameService;
import de.pokersim.domain.Card;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.GamePhase;
import de.pokersim.domain.Rank;
import de.pokersim.domain.Suit;
import de.pokersim.infrastructure.FixedRandomSource;
import de.pokersim.infrastructure.InMemoryGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer {@link GamePresenter}.
 *
 * <p>Prueft drei Bereiche:</p>
 * <ol>
 *   <li><strong>Karten versteckt</strong> — Hole-Cards werden als {@code ?? ??}
 *       angezeigt, solange die Phase nicht SHOWDOWN oder FINISHED ist.</li>
 *   <li><strong>Community-Labels</strong> — Flop-, Turn- und River-Karten werden
 *       mit korrektem Label formatiert.</li>
 *   <li><strong>Folded-Status</strong> — Gefoldete Spieler erscheinen mit dem
 *       Marker {@code [folded]} in der Spielerliste.</li>
 * </ol>
 */
class GamePresenterTest {

    private GameService service;
    private GamePresenter presenter;

    @BeforeEach
    void setUp() {
        service = new GameService(new InMemoryGameRepository(), FixedRandomSource.noShuffle());
        presenter = new GamePresenter();
    }

    // =========================================================================
    // Hole-Cards: versteckt vs. sichtbar
    // =========================================================================

    @Test
    @DisplayName("hole cards are hidden in PRE_FLOP phase")
    void holeCardsHiddenInPreFlop() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertEquals(GamePhase.PRE_FLOP.name(), vm.phase());
        for (String playerEntry : vm.players()) {
            assertTrue(playerEntry.contains("?? ??"),
                    "hole cards should be hidden in PRE_FLOP: " + playerEntry);
        }
    }

    @Test
    @DisplayName("hole cards are hidden in FLOP phase")
    void holeCardsHiddenInFlop() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        Game game = service.getGame(id);

        assertEquals(GamePhase.FLOP, game.phase());
        GameViewModel vm = presenter.present(game);

        for (String playerEntry : vm.players()) {
            assertTrue(playerEntry.contains("?? ??"),
                    "hole cards should be hidden in FLOP: " + playerEntry);
        }
    }

    @Test
    @DisplayName("hole cards are hidden in TURN phase")
    void holeCardsHiddenInTurn() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        service.advancePhase(id);
        Game game = service.getGame(id);

        assertEquals(GamePhase.TURN, game.phase());
        GameViewModel vm = presenter.present(game);

        for (String playerEntry : vm.players()) {
            assertTrue(playerEntry.contains("?? ??"),
                    "hole cards should be hidden in TURN: " + playerEntry);
        }
    }

    @Test
    @DisplayName("hole cards are hidden in RIVER phase")
    void holeCardsHiddenInRiver() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        service.advancePhase(id);
        service.advancePhase(id);
        Game game = service.getGame(id);

        assertEquals(GamePhase.RIVER, game.phase());
        GameViewModel vm = presenter.present(game);

        for (String playerEntry : vm.players()) {
            assertTrue(playerEntry.contains("?? ??"),
                    "hole cards should be hidden in RIVER: " + playerEntry);
        }
    }

    @Test
    @DisplayName("hole cards are revealed in SHOWDOWN phase")
    void holeCardsRevealedInShowdown() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        service.advancePhase(id);
        service.advancePhase(id);
        service.advancePhase(id);
        Game game = service.getGame(id);

        assertEquals(GamePhase.SHOWDOWN, game.phase());
        GameViewModel vm = presenter.present(game);

        for (String playerEntry : vm.players()) {
            assertFalse(playerEntry.contains("?? ??"),
                    "hole cards should be revealed in SHOWDOWN: " + playerEntry);
        }
    }

    @Test
    @DisplayName("player entry contains 'hand:' label in all phases")
    void playerEntryAlwaysContainsHandLabel() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        for (String playerEntry : vm.players()) {
            assertTrue(playerEntry.contains("hand:"),
                    "player entry should include hand label: " + playerEntry);
        }
    }

    // =========================================================================
    // Community-Labels
    // =========================================================================

    @Test
    @DisplayName("community label shows placeholder when no cards dealt yet")
    void communityLabelEmptyInPreFlop() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertTrue(vm.communityCards().isEmpty(),
                "no community cards should be dealt in PRE_FLOP");
        assertTrue(vm.communityLabel().contains("no community cards"),
                "label should indicate no cards: " + vm.communityLabel());
    }

    @Test
    @DisplayName("community label shows 'Flop:' with 3 cards after first advance")
    void communityLabelShowsFlopAfterFirstAdvance() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertEquals(3, vm.communityCards().size(),
                "exactly 3 community cards on flop");
        assertTrue(vm.communityLabel().startsWith("Flop:"),
                "label should start with 'Flop:': " + vm.communityLabel());
        assertFalse(vm.communityLabel().contains("Turn:"),
                "Turn should not appear yet: " + vm.communityLabel());
        assertFalse(vm.communityLabel().contains("River:"),
                "River should not appear yet: " + vm.communityLabel());
    }

    @Test
    @DisplayName("community label shows 'Flop:' and 'Turn:' after second advance")
    void communityLabelShowsFlopAndTurn() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        service.advancePhase(id);
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertEquals(4, vm.communityCards().size(),
                "exactly 4 community cards on turn");
        assertTrue(vm.communityLabel().contains("Flop:"),
                "label should contain 'Flop:': " + vm.communityLabel());
        assertTrue(vm.communityLabel().contains("Turn:"),
                "label should contain 'Turn:': " + vm.communityLabel());
        assertFalse(vm.communityLabel().contains("River:"),
                "River should not appear yet: " + vm.communityLabel());
    }

    @Test
    @DisplayName("community label shows 'Flop:', 'Turn:' and 'River:' after third advance")
    void communityLabelShowsFlopTurnRiver() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        service.advancePhase(id);
        service.advancePhase(id);
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertEquals(5, vm.communityCards().size(),
                "exactly 5 community cards on river");
        assertTrue(vm.communityLabel().contains("Flop:"),
                "label should contain 'Flop:': " + vm.communityLabel());
        assertTrue(vm.communityLabel().contains("Turn:"),
                "label should contain 'Turn:': " + vm.communityLabel());
        assertTrue(vm.communityLabel().contains("River:"),
                "label should contain 'River:': " + vm.communityLabel());
    }

    @Test
    @DisplayName("community label sections are separated by '|'")
    void communityLabelSectionsUsePipeSeparator() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        service.advancePhase(id);
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        int flopIndex = vm.communityLabel().indexOf("Flop:");
        int turnIndex = vm.communityLabel().indexOf("Turn:");
        assertTrue(flopIndex < turnIndex, "Flop should come before Turn in label");
        String between = vm.communityLabel().substring(flopIndex, turnIndex);
        assertTrue(between.contains("|"),
                "sections should be separated by '|': " + vm.communityLabel());
    }

    @Test
    @DisplayName("community card strings use suit codes C/D/H/S")
    void communityCardsFormattedWithSuitCode() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        service.advancePhase(id);
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        for (String card : vm.communityCards()) {
            assertFalse(card.isBlank(), "card string should not be blank");
            assertTrue(card.matches(".*[CDHS]"),
                    "card should end with a suit code: " + card);
        }
    }

    @Test
    @DisplayName("pot is rendered with 'Pot:' prefix")
    void rendersPotWithPrefix() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertTrue(vm.pot().startsWith("Pot:"),
                "pot should start with 'Pot:': " + vm.pot());
    }

    // =========================================================================
    // Folded-Status
    // =========================================================================

    @Test
    @DisplayName("folded player entry contains '[folded]' marker")
    void foldedPlayerShowsFoldedMarker() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);
        game.fold(game.players().stream()
                .filter(p -> p.name().equals("Alice"))
                .findFirst().orElseThrow().id());

        GameViewModel vm = presenter.present(game);

        String aliceEntry = vm.players().stream()
                .filter(e -> e.contains("Alice"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Alice not found in player list"));

        assertTrue(aliceEntry.contains("[folded]"),
                "folded player should have [folded] marker: " + aliceEntry);
    }

    @Test
    @DisplayName("active player entry does NOT contain '[folded]' marker")
    void activePlayerHasNoFoldedMarker() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);
        game.fold(game.players().stream()
                .filter(p -> p.name().equals("Alice"))
                .findFirst().orElseThrow().id());

        GameViewModel vm = presenter.present(game);

        String bobEntry = vm.players().stream()
                .filter(e -> e.contains("Bob"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Bob not found in player list"));

        assertFalse(bobEntry.contains("[folded]"),
                "active player should not have [folded] marker: " + bobEntry);
    }

    @Test
    @DisplayName("folded player hole cards remain hidden before SHOWDOWN")
    void foldedPlayerCardsHiddenBeforeShowdown() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);
        game.fold(game.players().stream()
                .filter(p -> p.name().equals("Alice"))
                .findFirst().orElseThrow().id());

        GameViewModel vm = presenter.present(game);

        String aliceEntry = vm.players().stream()
                .filter(e -> e.contains("Alice"))
                .findFirst().orElseThrow();

        assertTrue(aliceEntry.contains("?? ??"),
                "folded player's cards should still be hidden before SHOWDOWN: " + aliceEntry);
    }

    @Test
    @DisplayName("player entry contains chip count")
    void playerEntryContainsChipCount() {
        GameId id = service.startGame(List.of("Alice", "Bob"));
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        for (String playerEntry : vm.players()) {
            assertTrue(playerEntry.contains("chips:"),
                    "player entry should show chip count: " + playerEntry);
        }
    }

    @Test
    @DisplayName("all player names appear in the player list")
    void allPlayerNamesInList() {
        GameId id = service.startGame(List.of("Alice", "Bob", "Carol"));
        Game game = service.getGame(id);

        GameViewModel vm = presenter.present(game);

        assertEquals(3, vm.players().size());
        for (String name : List.of("Alice", "Bob", "Carol")) {
            assertTrue(vm.players().stream().anyMatch(e -> e.contains(name)),
                    name + " should appear in player list");
        }
    }

    // =========================================================================
    // formatCards (public helper)
    // =========================================================================

    @Test
    @DisplayName("formatCards returns empty list for empty input")
    void formatCardsEmptyInput() {
        List<String> result = presenter.formatCards(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("formatCards formats TWO of CLUBS as '2C'")
    void formatCardsTwoOfClubs() {
        List<String> result = presenter.formatCards(
                List.of(new Card(Rank.TWO, Suit.CLUBS)));

        assertEquals(List.of("2C"), result);
    }

    @Test
    @DisplayName("formatCards formats ACE of SPADES as 'AS'")
    void formatCardsAceOfSpades() {
        List<String> result = presenter.formatCards(
                List.of(new Card(Rank.ACE, Suit.SPADES)));

        assertEquals(List.of("AS"), result);
    }

    @Test
    @DisplayName("formatCards formats TEN of HEARTS as '10H'")
    void formatCardsTenOfHearts() {
        List<String> result = presenter.formatCards(
                List.of(new Card(Rank.TEN, Suit.HEARTS)));

        assertEquals(List.of("10H"), result);
    }

    @Test
    @DisplayName("formatCards covers all face cards: J, Q, K")
    void formatCardsFaceCards() {
        List<String> result = presenter.formatCards(List.of(
                new Card(Rank.JACK,  Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.KING,  Suit.HEARTS)
        ));

        assertEquals(List.of("JC", "QD", "KH"), result);
    }

    @Test
    @DisplayName("formatCards returns one entry per card in input order")
    void formatCardsCountAndOrderMatchInput() {
        List<String> result = presenter.formatCards(List.of(
                new Card(Rank.KING,  Suit.DIAMONDS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.JACK,  Suit.SPADES)
        ));

        assertEquals(3, result.size());
        assertEquals("KD", result.get(0));
        assertEquals("QH", result.get(1));
        assertEquals("JS", result.get(2));
    }

    @Test
    @DisplayName("formatCards covers all four suits correctly")
    void formatCardsAllSuits() {
        List<String> result = presenter.formatCards(List.of(
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.SPADES)
        ));

        assertEquals(List.of("AC", "AD", "AH", "AS"), result);
    }
}
