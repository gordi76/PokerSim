package de.pokersim.cli;

import de.pokersim.adapters.CommandParser;
import de.pokersim.adapters.GameController;
import de.pokersim.adapters.GamePresenter;
import de.pokersim.application.GameService;
import de.pokersim.infrastructure.FileGameRepository;
import de.pokersim.infrastructure.GameRepository;
import de.pokersim.infrastructure.JavaRandomSource;
import de.pokersim.infrastructure.RandomSource;

import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        GameRepository gameRepository = new FileGameRepository(Path.of("build", "pokersim-game.txt"));
        RandomSource randomSource = new JavaRandomSource();

        GameService gameService = new GameService(gameRepository, randomSource);
        GamePresenter gamePresenter = new GamePresenter();
        GameController gameController = new GameController(gameService, gamePresenter);

        PokerCli pokerCli = new PokerCli(
                new SystemConsoleIO(),
                new CommandParser(),
                gameController
        );

        pokerCli.run();
    }
}