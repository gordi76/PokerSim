package de.pokersim.domain;

public final class InvalidPlayerActionException extends RuntimeException {
    public InvalidPlayerActionException(String message) {
        super(message);
    }
}