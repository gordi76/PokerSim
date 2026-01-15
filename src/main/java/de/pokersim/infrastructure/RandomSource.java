package de.pokersim.infrastructure;

public interface RandomSource {
    int nextInt(int upperBoundExclusive);
}