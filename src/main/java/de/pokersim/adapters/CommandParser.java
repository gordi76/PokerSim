package de.pokersim.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommandParser {

    public ParsedCommand parse(String input) {
        if (input == null || input.isBlank()) {
            return new ParsedCommand("unknown", List.of());
        }

        String trimmedInput = input.trim();
        String[] parts = trimmedInput.split("\\s+");
        String command = parts[0].toLowerCase();

        List<String> arguments = new ArrayList<>();

        if (parts.length > 1) {
            arguments.addAll(Arrays.asList(parts).subList(1, parts.length));
        }

        return new ParsedCommand(command, arguments);
    }

    public record ParsedCommand(String name, List<String> arguments) {
    }
}