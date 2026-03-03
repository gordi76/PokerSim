package de.pokersim.infrastructure;

import de.pokersim.domain.Game;
import de.pokersim.domain.GameId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fake/Spy-Implementierung von {@link GameRepository} fuer Tests.
 *
 * <p>Verhaelt sich wie ein In-Memory-Repository, zaehlt aber zusaetzlich
 * mit, wie oft {@code save()} und {@code findById()} aufgerufen wurden.
 * So koennen Use-Case-Tests pruefen, ob die Persistenz tatsaechlich
 * angesprochen wurde, ohne ein Mocking-Framework zu nutzen.</p>
 *
 * <p>(Protokoll Kapitel 5: zweite Fake/Mock-Implementierung ohne
 * Framework.)</p>
 */
public final class FakeGameRepository implements GameRepository {

    private final Map<GameId, Game> store = new HashMap<>();
    private int saveCount;
    private int findCount;
    private Game lastSaved;

    @Override
    public void save(Game game) {
        store.put(game.id(), game);
        lastSaved = game;
        saveCount++;
    }

    @Override
    public Optional<Game> findById(GameId id) {
        findCount++;
        return Optional.ofNullable(store.get(id));
    }

    public int saveCount() {
        return saveCount;
    }

    public int findCount() {
        return findCount;
    }

    public Game lastSaved() {
        return lastSaved;
    }
}
