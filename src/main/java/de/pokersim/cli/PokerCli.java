package de.pokersim.cli;

import de.pokersim.adapters.CommandParser;
import de.pokersim.adapters.GameController;
import de.pokersim.adapters.GameViewModel;

import java.util.List;

public final class PokerCli {
    private final ConsoleIO consoleIO;
    private final CommandParser commandParser;
    private final GameController gameController;

    public PokerCli(ConsoleIO consoleIO, CommandParser commandParser, GameController gameController) {
        this.consoleIO = consoleIO;
        this.commandParser = commandParser;
        this.gameController = gameController;
    }

    public void run() {
        printWelcome();
        boolean running = true;

        while (running) {
            consoleIO.printLine("");
            consoleIO.printLine("Command:");
            String input = consoleIO.readLine();
            CommandParser.ParsedCommand command = commandParser.parse(input);

            try {
                running = handle(command);
            } catch (RuntimeException exception) {
                consoleIO.printLine("Error: " + exception.getMessage());
            }
        }

        consoleIO.printLine("Goodbye.");
    }

    private boolean handle(CommandParser.ParsedCommand command) {
        return switch (command.name()) {
            case "start" -> {
                GameViewModel viewModel = gameController.startGame(playerNamesFrom(command.arguments()));
                printGame(viewModel);
                yield true;
            }
            case "next" -> {
                GameViewModel viewModel = gameController.advancePhase();
                printGame(viewModel);
                yield true;
            }
            case "show" -> {
                GameViewModel viewModel = gameController.showCurrentGame();
                printGame(viewModel);
                yield true;
            }
            case "help" -> {
                printHelp();
                yield true;
            }
            case "exit" -> false;
            default -> {
                consoleIO.printLine("Unknown command. Type 'help' for available commands.");
                yield true;
            }
        };
    }

    private List<String> playerNamesFrom(List<String> arguments) {
        if (arguments.size() >= 2) {
            return arguments;
        }

        return List.of("Alice", "Bob");
    }

    private void printWelcome() {
        consoleIO.printLine("PokerSim - Texas Hold'em CLI");
        printHelp();
    }

    private void printHelp() {
        consoleIO.printLine("Commands:");
        consoleIO.printLine("  start Alice Bob Charlie  -> starts a new game");
        consoleIO.printLine("  next                     -> advances to the next phase");
        consoleIO.printLine("  show                     -> shows the current game");
        consoleIO.printLine("  exit                     -> closes the application");
    }

    private void printGame(GameViewModel viewModel) {
        consoleIO.printLine("");
        consoleIO.printLine("Game: " + viewModel.gameId());
        consoleIO.printLine("Phase: " + viewModel.phase());
        consoleIO.printLine(viewModel.pot());

        consoleIO.printLine("Community cards: " + viewModel.communityCards());

        consoleIO.printLine("Players:");
        for (String player : viewModel.players()) {
            consoleIO.printLine("  - " + player);
        }
    }
}