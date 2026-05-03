package de.pokersim.adapters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    @Test
    @DisplayName("parses 'start' with multiple player names")
    void parsesStartWithPlayerNames() {
        CommandParser.ParsedCommand command = parser.parse("start Alice Bob Charlie");

        assertEquals("start", command.name());
        assertEquals(3, command.arguments().size());
        assertEquals("Alice", command.arguments().get(0));
        assertEquals("Bob", command.arguments().get(1));
        assertEquals("Charlie", command.arguments().get(2));
    }

    @Test
    @DisplayName("normalises the command name to lower case")
    void normalisesCaseToLower() {
        CommandParser.ParsedCommand command = parser.parse("BET Alice 100");

        assertEquals("bet", command.name());
        assertEquals("Alice", command.arguments().get(0));
        assertEquals("100", command.arguments().get(1));
    }

    @Test
    @DisplayName("treats blank input as 'unknown'")
    void blankInputIsUnknown() {
        assertEquals("unknown", parser.parse("   ").name());
        assertEquals("unknown", parser.parse(null).name());
    }

    @Test
    @DisplayName("collapses multiple whitespace characters between arguments")
    void collapsesWhitespace() {
        CommandParser.ParsedCommand command = parser.parse("start   Alice    Bob");
        assertEquals(2, command.arguments().size());
    }
}
