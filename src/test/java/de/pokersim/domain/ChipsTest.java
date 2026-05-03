package de.pokersim.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChipsTest {

    @Test
    @DisplayName("plus returns a new Chips instance with the sum")
    void plusReturnsNewInstance() {
        Chips a = new Chips(100);
        Chips b = new Chips(50);

        Chips sum = a.plus(b);

        assertEquals(150, sum.amount());
        assertEquals(100, a.amount());
        assertEquals(50, b.amount());
    }

    @Test
    @DisplayName("minus rejects results that would go negative")
    void minusRejectsNegativeResult() {
        Chips small = new Chips(10);
        Chips large = new Chips(100);

        assertThrows(IllegalArgumentException.class, () -> small.minus(large));
    }

    @Test
    @DisplayName("constructor rejects negative amounts")
    void constructorRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Chips(-1));
    }

    @Test
    @DisplayName("compareTo follows numeric ordering")
    void comparesByNumericValue() {
        Chips small = new Chips(10);
        Chips large = new Chips(50);

        assertTrue(small.compareTo(large) < 0);
        assertTrue(large.compareTo(small) > 0);
        assertEquals(0, small.compareTo(new Chips(10)));
    }
}
