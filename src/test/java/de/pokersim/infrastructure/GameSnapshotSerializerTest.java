package de.pokersim.infrastructure;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSnapshotSerializerTest {

    @Test
    void serializesSnapshotHeader() {
        GameSnapshotSerializer serializer = new GameSnapshotSerializer();

        String content = serializer.serialize(exampleSnapshot());

        assertTrue(content.startsWith("POKERSIM_GAME_SNAPSHOT"));
    }

    @Test
    void serializesGameIdPhaseAndPot() {
        GameSnapshotSerializer serializer = new GameSnapshotSerializer();

        String content = serializer.serialize(exampleSnapshot());

        assertTrue(content.contains("gameId=game-1"));
        assertTrue(content.contains("phase=FLOP"));
        assertTrue(content.contains("pot=120"));
    }

    @Test
    void serializesCommunityCards() {
        GameSnapshotSerializer serializer = new GameSnapshotSerializer();

        String content = serializer.serialize(exampleSnapshot());

        assertTrue(content.contains("communityCards=ACE:SPADES,KING:HEARTS"));
    }

    @Test
    void serializesPlayers() {
        GameSnapshotSerializer serializer = new GameSnapshotSerializer();

        String content = serializer.serialize(exampleSnapshot());

        assertTrue(content.contains("player=player-1|Alice|900|false|TWO:CLUBS,THREE:DIAMONDS"));
        assertTrue(content.contains("player=player-2|Bob|850|true|FOUR:HEARTS,FIVE:SPADES"));
    }

    private GameSnapshot exampleSnapshot() {
        return new GameSnapshot(
                "game-1",
                "FLOP",
                120,
                List.of(
                        new PlayerSnapshot(
                                "player-1",
                                "Alice",
                                900,
                                false,
                                List.of(
                                        new CardSnapshot("TWO", "CLUBS"),
                                        new CardSnapshot("THREE", "DIAMONDS")
                                )
                        ),
                        new PlayerSnapshot(
                                "player-2",
                                "Bob",
                                850,
                                true,
                                List.of(
                                        new CardSnapshot("FOUR", "HEARTS"),
                                        new CardSnapshot("FIVE", "SPADES")
                                )
                        )
                ),
                List.of(
                        new CardSnapshot("ACE", "SPADES"),
                        new CardSnapshot("KING", "HEARTS")
                )
        );
    }
}