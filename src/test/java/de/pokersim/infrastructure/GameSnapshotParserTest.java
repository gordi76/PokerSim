package de.pokersim.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSnapshotParserTest {

    @Test
    void parsesGameIdPhaseAndPot() {
        GameSnapshotParser parser = new GameSnapshotParser();

        GameSnapshot snapshot = parser.parse(exampleContent());

        assertEquals("game-1", snapshot.gameId());
        assertEquals("TURN", snapshot.phase());
        assertEquals(200, snapshot.pot());
    }

    @Test
    void parsesCommunityCards() {
        GameSnapshotParser parser = new GameSnapshotParser();

        GameSnapshot snapshot = parser.parse(exampleContent());

        assertEquals(3, snapshot.communityCardCount());
        assertEquals("ACE", snapshot.communityCards().get(0).rank());
        assertEquals("SPADES", snapshot.communityCards().get(0).suit());
    }

    @Test
    void parsesPlayers() {
        GameSnapshotParser parser = new GameSnapshotParser();

        GameSnapshot snapshot = parser.parse(exampleContent());

        assertEquals(2, snapshot.playerCount());

        PlayerSnapshot alice = snapshot.players().get(0);
        assertEquals("player-1", alice.playerId());
        assertEquals("Alice", alice.name());
        assertEquals(900, alice.chips());
        assertFalse(alice.folded());
        assertEquals(2, alice.holeCardCount());
    }

    @Test
    void parsesFoldedPlayer() {
        GameSnapshotParser parser = new GameSnapshotParser();

        GameSnapshot snapshot = parser.parse(exampleContent());

        PlayerSnapshot bob = snapshot.players().get(1);

        assertEquals("Bob", bob.name());
        assertTrue(bob.folded());
    }

    @Test
    void rejectsInvalidHeader() {
        GameSnapshotParser parser = new GameSnapshotParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse("INVALID"));
    }

    @Test
    void rejectsBlankContent() {
        GameSnapshotParser parser = new GameSnapshotParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
    }

    private String exampleContent() {
        return ""
                + "POKERSIM_GAME_SNAPSHOT\n"
                + "gameId=game-1\n"
                + "phase=TURN\n"
                + "pot=200\n"
                + "communityCards=ACE:SPADES,KING:HEARTS,QUEEN:DIAMONDS\n"
                + "player=player-1|Alice|900|false|TWO:CLUBS,THREE:DIAMONDS\n"
                + "player=player-2|Bob|850|true|FOUR:HEARTS,FIVE:SPADES\n";
    }
}