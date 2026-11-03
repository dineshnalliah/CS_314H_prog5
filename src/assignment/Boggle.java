package assignment;

import java.util.*;
import java.awt.Point;
import java.io.*;

public class Boggle {

    static final int TURN_TIME = 5_000;

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            int numberOfPlayers = getPlayerCount(in);

            GameManager game = new GameManager();
            
            GameDictionary dictionary = new GameDictionary();
            dictionary.loadDictionary("words.txt");

            game.newGame(4, numberOfPlayers, "cubes.txt", dictionary);
            game.setGame(Main.test);
            game.setSearchTactic(BoggleGame.SearchTactic.SEARCH_DICT);

            for (int player = 0; player < numberOfPlayers; player++) {

                long targetTime = System.currentTimeMillis() + TURN_TIME;
                while (System.currentTimeMillis() < targetTime) {
                    char[][] board = game.getBoard();
                    List<Point> path = game.getLastAddedWord();
                    if (!playTurn(player, board, path, in, game, targetTime)) break;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                game.resetPath();
                waitForPlayer(in, player+1, numberOfPlayers);
            }

            showResults(numberOfPlayers, game);

            in.close();
        }
        catch (IOException e) {

        }
        
    }

    private static int getPlayerCount(BufferedReader in) throws IOException {
        System.out.print("How many players (default 1): ");
        String input = in.readLine();
        clearConsole();
        if (!input.matches("\\d+")) return 1; // make constant
        return Integer.parseInt(input);
    }

    private static void waitForPlayer(BufferedReader in, int player, int totalPlayers) throws IOException {
        clearConsole();
        if (player == totalPlayers) return;
        System.out.println("Player " + player + "\'s turn, press ENTER to confirm: ");
        in.readLine();
    }

    private static void printBoard(char[][] board, List<Point> path) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (path != null && path.contains(new Point(i, j))) {
                    System.out.print(Character.toUpperCase(board[i][j]) + " ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        } // make constant SIZE
    }

    private static boolean playTurn(int player, char[][] board, List<Point> path, BufferedReader in, GameManager game, long deadline) throws IOException {
        printBoard(board, path);

        System.out.print("Player " + (player+1) + " guesses: ");
        String input = in.readLine();
        if (input == null) return false;

        // add this: addWord now returns 0 for repeats
        boolean alreadyGuessed = game.getWords(player).contains(input.toLowerCase());
        int score = game.addWord(input, player);

        clearConsole();
        if (score > 0) System.out.println(score + " points added");
        else if (alreadyGuessed) System.out.println("already guessed");
        else System.out.println("invalid word");
        return true;

    }

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


    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

}


