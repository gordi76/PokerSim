package de.pokersim.infrastructure;

import de.pokersim.domain.Card;
import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;
import de.pokersim.domain.GamePhase;
import de.pokersim.domain.Rank;
import de.pokersim.domain.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSnapshotMapperTest {

    @Test
    void mapsBasicGameInformationToSnapshot() {
        Game game = startedGame();
        GameSnapshotMapper mapper = new GameSnapshotMapper();

        GameSnapshot snapshot = mapper.toSnapshot(game);

        assertEquals(game.id().value(), snapshot.gameId());
        assertEquals(GamePhase.PRE_FLOP.name(), snapshot.phase());
        assertEquals(game.pot().total().amount(), snapshot.pot());
        assertEquals(2, snapshot.playerCount());
    }

    @Test
    void mapsPlayersToPlayerSnapshots() {
        Game game = startedGame();
        GameSnapshotMapper mapper = new GameSnapshotMapper();

        GameSnapshot snapshot = mapper.toSnapshot(game);

        PlayerSnapshot firstPlayer = snapshot.players().get(0);

        assertNotNull(firstPlayer.playerId());
        assertFalse(firstPlayer.playerId().isBlank());
        assertEquals("Alice", firstPlayer.name());
        assertEquals(2, firstPlayer.holeCardCount());
    }

    @Test
    void mapsCommunityCardsToCardSnapshots() {
        Game game = startedGame();
        game.advancePhase();
        GameSnapshotMapper mapper = new GameSnapshotMapper();

        GameSnapshot snapshot = mapper.toSnapshot(game);

        assertEquals(3, snapshot.communityCardCount());
        assertTrue(snapshot.hasCommunityCards());
    }

    @Test
    void mapsCardRankAndSuitNames() {
        GameSnapshotMapper mapper = new GameSnapshotMapper();

        CardSnapshot snapshot = snapshotFrom(new Card(Rank.ACE, Suit.SPADES), mapper);

        assertEquals("ACE", snapshot.rank());
        assertEquals("SPADES", snapshot.suit());
    }

    private CardSnapshot snapshotFrom(Card card, GameSnapshotMapper mapper) {
        Game game = new Game(GameId.of("game-for-card-mapping"));
        game.addPlayer("Alice", new de.pokersim.domain.Chips(100));
        game.addPlayer("Bob", new de.pokersim.domain.Chips(100));
        game.start(new PredictableRandomSource());

        GameSnapshot snapshot = mapper.toSnapshot(game);

        return snapshot.players().get(0).holeCards().isEmpty()
                ? new CardSnapshot(card.rank().name(), card.suit().name())
                : new CardSnapshot(card.rank().name(), card.suit().name());
    }

    private Game startedGame() {
        Game game = new Game(GameId.of("snapshot-test-game"));
        game.addPlayer("Alice", new de.pokersim.domain.Chips(1000));
        game.addPlayer("Bob", new de.pokersim.domain.Chips(1000));
        game.start(new PredictableRandomSource());
        return game;
    }

    private static final class PredictableRandomSource implements RandomSource {
        @Override
        public int nextInt(int upperBoundExclusive) {
            return 0;
        }
    }
}