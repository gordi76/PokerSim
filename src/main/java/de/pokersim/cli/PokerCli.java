package de.pokersim.cli;

import de.pokersim.adapters.CommandParser;
import de.pokersim.adapters.GameController;
import de.pokersim.adapters.GameViewModel;
import de.pokersim.adapters.ShowdownResult;

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
            if (input == null) {
                break;
            }
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
            case "bet" -> {
                if (command.arguments().size() < 2) {
                    consoleIO.printLine("Usage: bet <player> <amount>");
                    yield true;
                }
                String playerName = command.arguments().get(0);
                int amount = parseInt(command.arguments().get(1));
                GameViewModel viewModel = gameController.placeBet(playerName, amount);
                printGame(viewModel);
                yield true;
            }
            case "fold" -> {
                if (command.arguments().isEmpty()) {
                    consoleIO.printLine("Usage: fold <player>");
                    yield true;
                }
                GameViewModel viewModel = gameController.fold(command.arguments().get(0));
                printGame(viewModel);
                yield true;
            }
            case "show" -> {
                GameViewModel viewModel = gameController.showCurrentGame();
                printGame(viewModel);
                yield true;
            }
            case "showdown" -> {
                ShowdownResult result = gameController.runShowdown();
                printShowdown(result);
                yield true;
            }
            case "help" -> {
                printHelp();
                yield true;
            }
            case "exit" -> false;
            case "" -> true;
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

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("'" + value + "' is not a valid amount (expected integer, e.g. 50)");
        }
    }

    private void printWelcome() {
        consoleIO.printLine("PokerSim - Texas Hold'em CLI");
        printHelp();
    }

    private void printHelp() {
        consoleIO.printLine("Commands:");
        consoleIO.printLine("  start Alice Bob Charlie  -> starts a new game");
        consoleIO.printLine("  next                     -> advances to the next phase");
        consoleIO.printLine("  bet <player> <amount>    -> player puts chips into the pot");
        consoleIO.printLine("  fold <player>            -> player gives up the round");
        consoleIO.printLine("  show                     -> shows the current game");
        consoleIO.printLine("  showdown                 -> evaluates hands and determines the winner");
        consoleIO.printLine("  help                     -> prints this help");
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

    private void printShowdown(ShowdownResult result) {
        consoleIO.printLine("");
        consoleIO.printLine("Showdown:");
        for (String summary : result.playerHandSummaries()) {
            consoleIO.printLine("  " + summary);
        }
        consoleIO.printLine("");
        consoleIO.printLine("Winner: " + result.winnerName());
        consoleIO.printLine(result.winnerName() + " wins " + result.chipsWon() + " chips.");
        consoleIO.printLine("Game phase: FINISHED");
    }
}