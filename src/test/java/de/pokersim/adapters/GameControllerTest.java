package de.pokersim.adapters;

import de.pokersim.application.GameService;
import de.pokersim.domain.GameCommand;
import de.pokersim.domain.GamePhase;
import de.pokersim.infrastructure.FixedRandomSource;
import de.pokersim.infrastructure.InMemoryGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer den {@link GameController}.
 *
 * <p>Prueft Validierungsregeln fuer bet und fold, die korrekte
 * Aufzeichnung der Command-History sowie die Phase-Validierung
 * durch das {@link de.pokersim.domain.PlayerAction}-Modell.</p>
 */
class GameControllerTest {

    private GameController controller;

    @BeforeEach
    void setUp() {
        var repo = new InMemoryGameRepository();
        var service = new GameService(repo, FixedRandomSource.noShuffle());
        controller = new GameController(service, new GamePresenter());
    }

    // -------------------------------------------------------------------------
    // No game started
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("bet without started game throws with helpful message")
    void betWithoutGameThrows() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> controller.placeBet("Alice", 50));

        assertTrue(ex.getMessage().contains("start Alice Bob"),
                "error should hint how to start a game");
    }

    @Test
    @DisplayName("fold without started game throws with helpful message")
    void foldWithoutGameThrows() {
        assertThrows(IllegalStateException.class,
                () -> controller.fold("Alice"));
    }

    @Test
    @DisplayName("history is empty before any game starts")
    void historyEmptyBeforeStart() {
        assertTrue(controller.commandHistory().isEmpty());
    }

    // -------------------------------------------------------------------------
    // bet validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("bet with valid amount records BetCommand in history")
    void betRecordsCommand() {
        controller.startGame(List.of("Alice", "Bob"));

        controller.placeBet("Alice", 50);

        List<GameCommand> history = controller.commandHistory();
        assertEquals(1, history.size());
        assertInstanceOf(GameCommand.BetCommand.class, history.get(0));
        GameCommand.BetCommand cmd = (GameCommand.BetCommand) history.get(0);
        assertEquals("Alice", cmd.playerName());
        assertEquals(50, cmd.amount());
    }

    @Test
    @DisplayName("bet with amount 0 throws with clear message")
    void betZeroAmountThrows() {
        controller.startGame(List.of("Alice", "Bob"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.placeBet("Alice", 0));

        assertTrue(ex.getMessage().toLowerCase().contains("greater than 0")
                || ex.getMessage().toLowerCase().contains("must be"),
                "error should explain the amount constraint");
    }

    @Test
    @DisplayName("bet with negative amount throws")
    void betNegativeAmountThrows() {
        controller.startGame(List.of("Alice", "Bob"));

        assertThrows(RuntimeException.class,
                () -> controller.placeBet("Alice", -10));
    }

    @Test
    @DisplayName("bet with more chips than player has throws with player name")
    void betMoreThanAvailableChipsThrows() {
        controller.startGame(List.of("Alice", "Bob"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.placeBet("Alice", 99_999));

        assertTrue(ex.getMessage().contains("Alice"),
                "error should name the player who lacks chips");
        assertTrue(ex.getMessage().contains("chips"),
                "error should mention chips");
    }

    @Test
    @DisplayName("bet on unknown player throws with available players listed")
    void betUnknownPlayerThrows() {
        controller.startGame(List.of("Alice", "Bob"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.placeBet("Charlie", 50));

        assertTrue(ex.getMessage().contains("Charlie"),
                "error should mention the unknown player name");
        assertTrue(ex.getMessage().contains("Alice"),
                "error should list available players");
    }

    @Test
    @DisplayName("bet after player has folded throws")
    void betAfterFoldThrows() {
        controller.startGame(List.of("Alice", "Bob"));
        controller.fold("Alice");

        assertThrows(IllegalStateException.class,
                () -> controller.placeBet("Alice", 50));
    }

    // -------------------------------------------------------------------------
    // fold validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("fold records FoldCommand in history")
    void foldRecordsCommand() {
        controller.startGame(List.of("Alice", "Bob"));

        controller.fold("Alice");

        List<GameCommand> history = controller.commandHistory();
        assertEquals(1, history.size());
        assertInstanceOf(GameCommand.FoldCommand.class, history.get(0));
        assertEquals("Alice", ((GameCommand.FoldCommand) history.get(0)).playerName());
    }

    @Test
    @DisplayName("folding the same player twice throws")
    void doubleFoldThrows() {
        controller.startGame(List.of("Alice", "Bob"));
        controller.fold("Alice");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> controller.fold("Alice"));

        assertTrue(ex.getMessage().contains("Alice"),
                "error should name the already-folded player");
    }

    @Test
    @DisplayName("fold unknown player throws with available players listed")
    void foldUnknownPlayerThrows() {
        controller.startGame(List.of("Alice", "Bob"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.fold("Nico"));

        assertTrue(ex.getMessage().contains("Nico"));
    }

    // -------------------------------------------------------------------------
    // Phase validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("bet in SHOWDOWN phase is rejected")
    void betInShowdownPhaseThrows() {
        controller.startGame(List.of("Alice", "Bob"));
        // Advance through PRE_FLOP -> FLOP -> TURN -> RIVER -> SHOWDOWN
        controller.advancePhase();
        controller.advancePhase();
        controller.advancePhase();
        controller.advancePhase();

        GameViewModel vm = controller.showCurrentGame();
        assertEquals(GamePhase.SHOWDOWN.name(), vm.phase());

        assertThrows(IllegalStateException.class,
                () -> controller.placeBet("Alice", 50));
    }

    @Test
    @DisplayName("fold in SHOWDOWN phase is rejected")
    void foldInShowdownPhaseThrows() {
        controller.startGame(List.of("Alice", "Bob"));
        controller.advancePhase();
        controller.advancePhase();
        controller.advancePhase();
        controller.advancePhase();

        assertThrows(IllegalStateException.class,
                () -> controller.fold("Alice"));
    }

    @Test
    @DisplayName("advance phase records AdvancePhaseCommand in history")
    void advancePhaseRecordsCommand() {
        controller.startGame(List.of("Alice", "Bob"));

        controller.advancePhase();

        List<GameCommand> history = controller.commandHistory();
        assertEquals(1, history.size());
        assertInstanceOf(GameCommand.AdvancePhaseCommand.class, history.get(0));
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("history grows in correct order for mixed actions")
    void historyOrderIsPreserved() {
        controller.startGame(List.of("Alice", "Bob"));

        controller.placeBet("Alice", 50);
        controller.fold("Bob");
        controller.advancePhase();

        List<GameCommand> history = controller.commandHistory();
        assertEquals(3, history.size());
        assertInstanceOf(GameCommand.BetCommand.class, history.get(0));
        assertInstanceOf(GameCommand.FoldCommand.class, history.get(1));
        assertInstanceOf(GameCommand.AdvancePhaseCommand.class, history.get(2));
    }

    @Test
    @DisplayName("history is unmodifiable")
    void historyIsUnmodifiable() {
        controller.startGame(List.of("Alice", "Bob"));
        List<GameCommand> history = controller.commandHistory();

        assertThrows(UnsupportedOperationException.class,
                () -> history.add(new GameCommand.AdvancePhaseCommand()));
    }

    @Test
    @DisplayName("showdown command is recorded in history")
    void showdownRecordsCommand() {
        controller.startGame(List.of("Alice", "Bob"));

        controller.runShowdown();

        List<GameCommand> history = controller.commandHistory();
        assertTrue(history.stream().anyMatch(c -> c instanceof GameCommand.ShowdownCommand),
                "showdown command should appear in history");
    }

    // -------------------------------------------------------------------------
    // show / hand
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("showCurrentGame returns PRE_FLOP phase after start")
    void showCurrentGameReturnsPreFlop() {
        controller.startGame(List.of("Alice", "Bob"));

        GameViewModel vm = controller.showCurrentGame();

        assertEquals(GamePhase.PRE_FLOP.name(), vm.phase());
    }

    @Test
    @DisplayName("getPlayerHand returns cards for active player")
    void getPlayerHandReturnsCards() {
        controller.startGame(List.of("Alice", "Bob"));

        String hand = controller.getPlayerHand("Alice");

        assertTrue(hand.contains("Alice"), "result should contain player name");
        assertTrue(hand.contains("hand"), "result should mention hand");
    }

    @Test
    @DisplayName("getPlayerHand for folded player reports folded")
    void getPlayerHandFolded() {
        controller.startGame(List.of("Alice", "Bob"));
        controller.fold("Alice");

        String hand = controller.getPlayerHand("Alice");

        assertTrue(hand.toLowerCase().contains("fold"),
                "result should indicate player has folded");
    }
}
