package de.pokersim.domain;

/**
 * Marker interface for all game commands.
 *
 * Each concrete command is an immutable record that captures exactly
 * the data needed for one player action. The history of executed
 * commands can be stored as {@code List<GameCommand>}.
 */
public sealed interface GameCommand
        permits GameCommand.BetCommand,
                GameCommand.FoldCommand,
                GameCommand.AdvancePhaseCommand,
                GameCommand.ShowdownCommand {

    /** The action type this command represents. */
    AllowedAction action();

    record BetCommand(String playerName, int amount) implements GameCommand {
        @Override
        public AllowedAction action() { return AllowedAction.BET; }

        @Override
        public String toString() { return "bet " + playerName + " " + amount; }
    }

    record FoldCommand(String playerName) implements GameCommand {
        @Override
        public AllowedAction action() { return AllowedAction.FOLD; }

        @Override
        public String toString() { return "fold " + playerName; }
    }

    record AdvancePhaseCommand() implements GameCommand {
        @Override
        public AllowedAction action() { return AllowedAction.ADVANCE_PHASE; }

        @Override
        public String toString() { return "next"; }
    }

    record ShowdownCommand() implements GameCommand {
        @Override
        public AllowedAction action() { return AllowedAction.SHOWDOWN; }

        @Override
        public String toString() { return "showdown"; }
    }
}
