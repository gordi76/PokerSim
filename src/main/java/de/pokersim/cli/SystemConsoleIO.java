package de.pokersim.cli;

import java.util.Scanner;

public final class SystemConsoleIO implements ConsoleIO {
    private final Scanner scanner;

    public SystemConsoleIO() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void printLine(String text) {
        System.out.println(text);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }
}