package de.pokersim.cli;

import de.pokersim.adapters.CommandParser;
import de.pokersim.adapters.GameController;
import de.pokersim.adapters.GamePresenter;
import de.pokersim.application.GameService;
import de.pokersim.infrastructure.FixedRandomSource;
import de.pokersim.infrastructure.InMemoryGameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PokerCliTest {

    @Test
    @DisplayName("help command is available before any game starts")
    void helpBeforeStart() {
        FakeConsoleIO console = new FakeConsoleIO("help", "exit");
        PokerCli cli = newCli(console);

        cli.run();

        assertTrue(console.printed().stream()
                        .anyMatch(line -> line.contains("start Alice Bob")),
                "help output should mention the start command");
    }

    @Test
    @DisplayName("a full demo run reaches the FINISHED phase")
    void fullDemoRunFinishesGame() {
        FakeConsoleIO console = new FakeConsoleIO(
                "start Alice Bob",
                "next",
                "next",
                "next",
                "next",
                "next",
                "exit"
        );
        PokerCli cli = newCli(console);

        cli.run();

        assertTrue(console.printed().stream()
                        .anyMatch(line -> line.contains("FINISHED")),
                "expected the demo run to end in FINISHED, output was: "
                        + console.printed());
    }

    @Test
    @DisplayName("unknown commands print a hint without crashing")
    void unknownCommandPrintsHint() {
        FakeConsoleIO console = new FakeConsoleIO("foobar", "exit");
        PokerCli cli = newCli(console);

        cli.run();

        assertTrue(console.printed().stream()
                .anyMatch(line -> line.contains("Unknown command")));
    }

    private PokerCli newCli(FakeConsoleIO console) {
        var repository = new InMemoryGameRepository();
        var service = new GameService(repository, FixedRandomSource.noShuffle());
        var controller = new GameController(service, new GamePresenter());
        return new PokerCli(console, new CommandParser(), controller);
    }

    private static final class FakeConsoleIO implements ConsoleIO {

        private final Deque<String> inputs = new ArrayDeque<>();
        private final List<String> printed = new ArrayList<>();

        FakeConsoleIO(String... scriptedInputs) {
            for (String input : scriptedInputs) {
                inputs.add(input);
            }
        }

        @Override
        public void printLine(String text) {
            printed.add(text);
        }

        @Override
        public String readLine() {
            return inputs.isEmpty() ? "exit" : inputs.poll();
        }

        List<String> printed() {
            return printed;
        }
    }
}
