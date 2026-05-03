package de.pokersim.infrastructure;


public final class FixedRandomSource implements RandomSource {
    private final int[] values;
    private int cursor;

    public FixedRandomSource(int... values) {
        this.values = values == null ? new int[]{0} : values.clone();
    }

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
