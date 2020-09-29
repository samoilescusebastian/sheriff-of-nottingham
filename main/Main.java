package com.tema1.main;

// just to trick checkstyle
import com.tema1.game.Game;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
        GameInput gameInput = gameInputLoader.load();
        Game game = new Game(gameInput);
        game.startGame();
        game.viewResults();
    }
}
