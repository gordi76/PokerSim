package de.pokersim.infrastructure;

/**
 * Deterministische {@link RandomSource} fuer Tests.
 *
 * <p>Statt echter Zufallszahlen liefert sie eine vorab festgelegte
 * Sequenz von int-Werten zurueck. Tests koennen dadurch exakt
 * vorhersagen, in welcher Reihenfolge das Deck nach {@code shuffle()}
 * aussieht und welche Karten gezogen werden.</p>
 *
 * <p>Bewusst <strong>kein</strong> Drittanbieter-Mocking-Framework,
 * sondern eine handgeschriebene Fake-Implementierung des
 * {@link RandomSource}-Interfaces (siehe Protokoll Kapitel 5:
 * Fake/Mock ohne Framework).</p>
 *
 * <p>Der Faktor {@code Math.floorMod(value, bound)} stellt sicher, dass
 * der Aufrufer immer einen gueltigen Index erhaelt - egal welche
 * Sequenz uebergeben wurde.</p>
 */
public final class FixedRandomSource implements RandomSource {

    private final int[] values;
    private int cursor;

    public FixedRandomSource(int... values) {
        this.values = values == null ? new int[]{0} : values.clone();
    }

    /**
     * Bequemer Konstruktor: nicht mischen.
     * Indem bei jedem Aufruf {@code 0} zurueckgegeben wird, bleibt die
     * Reihenfolge der Karten unveraendert (Original-Deck-Reihenfolge).
     */
    public static FixedRandomSource noShuffle() {
        return new FixedRandomSource(0);
    }

    @Override
    public int nextInt(int upperBoundExclusive) {
        if (upperBoundExclusive <= 0) {
            throw new IllegalArgumentException("upperBoundExclusive must be positive");
        }
        if (values.length == 0) {
            return 0;
        }
        int rawValue = values[cursor % values.length];
        cursor++;
        return Math.floorMod(rawValue, upperBoundExclusive);
    }
}
