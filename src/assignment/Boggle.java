package assignment;

import java.util.*;
import java.awt.Point;
import java.io.*;

public class Boggle {

    static final int TURN_TIME = 60_000;

    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);
            
            int numberOfPlayers = getPlayerCount(in);

            GameManager game = new GameManager();
            
            GameDictionary dictionary = new GameDictionary();
            dictionary.loadDictionary("words.txt");

            game.newGame(4, numberOfPlayers, "cubes.txt", dictionary);
            game.setSearchTactic(BoggleGame.SearchTactic.SEARCH_BOARD);

            for (int player = 0; player < numberOfPlayers; player++) {

                long targetTime = System.currentTimeMillis() + TURN_TIME;
                while (System.currentTimeMillis() < targetTime) {
                    char[][] board = game.getBoard();
                    List<Point> path = game.getLastAddedWord();
                    playTurn(player, board, path, in, game);

                    try {
                        // Sleep for 500ms to yield the CPU
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                        break;
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

    private static int getPlayerCount(Scanner in) {
        System.out.print("How many players: ");
        String input = in.next();
        clearConsole();
        if (!input.matches("-?\\d+")) return 1; // make constant
        return Integer.parseInt(input);
    }

    private static void waitForPlayer(Scanner in, int player, int totalPlayers) {
        clearConsole();
        if (player == totalPlayers) return;
        System.out.println("Player " + player + "\'s turn, press ENTER to confirm: ");
        in.nextLine();
        in.nextLine();
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

    private static void playTurn(int player, char[][] board, List<Point> path, Scanner in, GameManager game) {
        printBoard(board, path);

        System.out.print("Player " + player + " guesses: ");
        String input = in.next();

        // add this: addWord now returns 0 for repeats
        boolean alreadyGuessed = game.getWords(player).contains(input.toLowerCase());
        int score = game.addWord(input, player);

        clearConsole();
        if (score > 0) System.out.println(score + " points added");
        else if (alreadyGuessed) System.out.println("already guessed");
        else System.out.println("invalid word");

    }

    private static void showResults(int totalPlayers, GameManager game) {
        clearConsole();
        for (int j = 0; j < totalPlayers; j++) {
                    System.out.println("Player " + j + ": " + game.getScores()[j]);
                    System.out.println("Words: " + game.getWords(j));
                    System.out.println();
                }
            System.out.println("\n\n");
            Collection<String> answers = game.getAllWords();
            int compScore = game.getComputerScore();
            System.out.println("Other possible words: " + answers);
            System.out.println("Computer score: " + compScore);
    }


    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

}


