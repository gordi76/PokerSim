package de.pokersim.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer den {@link Pot}.
 *
 * <p>Verteidigen die Invarianten der Pot-Domaen-Klasse: Chips kommen
 * dazu, der Pot ist nach Auszahlung wieder leer, und die ausgezahlte
 * Summe entspricht dem letzten Stand.</p>
 */
class PotTest {

    @Test
    @DisplayName("a fresh pot is empty")
    void freshPotIsEmpty() {
        Pot pot = new Pot();

        assertTrue(pot.isEmpty());
        assertEquals(0, pot.total().amount());
    }

    @Test
    @DisplayName("add accumulates chips")
    void addAccumulatesChips() {
        Pot pot = new Pot();

        pot.add(new Chips(50));
        pot.add(new Chips(75));

        assertEquals(125, pot.total().amount());
    }

    @Test
    @DisplayName("payOut empties the pot and returns the previous total")
    void payOutEmptiesPot() {
        Pot pot = new Pot();
        pot.add(new Chips(200));

        Chips paidOut = pot.payOut();

        assertEquals(200, paidOut.amount());
        assertTrue(pot.isEmpty());
    }
}
