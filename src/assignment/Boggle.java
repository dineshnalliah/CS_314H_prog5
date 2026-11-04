package assignment;

import java.util.*;
import java.awt.Point;
import java.io.*;

public class Boggle {

    static final int TURN_TIME = 60_000;
    static final int BOARD_SIZE = 4;
    static final String QUIT = "q";

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            GameDictionary dictionary = new GameDictionary();
            dictionary.loadDictionary("words.txt");

            boolean playAgain = true;
            while (playAgain) {
                int numberOfPlayers = getPlayerCount(in);

                GameManager game = new GameManager();
                game.newGame(BOARD_SIZE, numberOfPlayers, "cubes.txt", dictionary);
                game.setSearchTactic(BoggleGame.SearchTactic.SEARCH_DICT);

                for (int player = 0; player < numberOfPlayers; player++) {
                    long targetTime = System.currentTimeMillis() + TURN_TIME;
                    while (System.currentTimeMillis() < targetTime) {
                        char[][] board = game.getBoard();
                        List<Point> path = game.getLastAddedWord();
                        if (!playTurn(player, board, path, in, game, targetTime)) break;
                    }
                    game.resetPath();
                    waitForPlayer(in, player+1, numberOfPlayers);
                }

                showResults(numberOfPlayers, game);
                playAgain = askPlayAgain(in);
            }

            in.close();
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Prompts the player if they want to play again
    private static boolean askPlayAgain(BufferedReader in) throws IOException {
        System.out.print("Play again? (y/n): ");
        String input = in.readLine();
        clearConsole();
        return input != null && input.trim().equalsIgnoreCase("y");
    }

    // Reads in how many players based off of user input
    private static int getPlayerCount(BufferedReader in) throws IOException {
        System.out.print("How many players (default 1): ");
        String input = in.readLine();
        clearConsole();
        
        // accept 1-999; anything else (blank, 0, letters, huge numbers) means 1 player
        if (input == null || !input.trim().matches("[1-9]\\d{0,2}")) return 1;
        return Integer.parseInt(input.trim());
    }

    // If multiplayer, waits for the next player to press enter before starting the game
    private static void waitForPlayer(BufferedReader in, int player, int totalPlayers) throws IOException {
        clearConsole();
        if (player == totalPlayers) return;
        System.out.println("Player " + (player + 1) + "\'s turn, press ENTER to confirm: ");
        in.readLine();
    }

    // Prints out board with specified word found in uppercase
    private static void printBoard(char[][] board, List<Point> path) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (path != null && path.contains(new Point(i, j))) {
                    System.out.print(Character.toUpperCase(board[i][j]) + " ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    // Gameplay for one turn
    private static boolean playTurn(int player, char[][] board, List<Point> path, BufferedReader in, GameManager game, long deadline) throws IOException {
        printBoard(board, path);

        long secondsLeft = Math.max(0, (deadline - System.currentTimeMillis()) / 1000);
        System.out.print("Player " + (player+1) + " guesses (" + secondsLeft + "s left, '" + QUIT + "' to finish): ");
        String input = in.readLine();
        if (input == null) return false;
        input = input.trim();
        if (input.equalsIgnoreCase(QUIT)) return false;

        // readLine() blocks, so the deadline can pass while the player is typing
        if (System.currentTimeMillis() > deadline) {
            clearConsole();
            System.out.println("Time's up! \"" + input + "\" was not counted.");
            return false;
        }

        // add this: addWord now returns 0 for repeats
        boolean alreadyGuessed = game.getWords(player).contains(input.toLowerCase());
        int score = game.addWord(input, player);

        clearConsole();
        if (score > 0) System.out.println(score + " points added");
        else if (alreadyGuessed) System.out.println("already guessed");
        else System.out.println("invalid word");
        return true;

    }

    // After turn is done, display player and computer results
    private static void showResults(int totalPlayers, GameManager game) {
        clearConsole();
        for (int j = 0; j < totalPlayers; j++) {
                    System.out.println("Player " + (j+1) + ": " + game.getScores()[j]);
                    System.out.println("Words: " + game.getWords(j));
                    System.out.println();
                }
            System.out.println("\n\n");

            Collection<String> all = game.getAllWords();
            Collection<String> answers = game.getComputerWords();

            int compScore = game.getComputerScore();

            System.out.println("Words on board: " + all.size());
            System.out.println("Words not found players (Computer Words): " + answers);
            System.out.println("Computer score: " + compScore);
    }


    // "Clears" the console by adding new lines for user readability
    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

}


