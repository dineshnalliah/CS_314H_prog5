package assignment;

import java.util.*;
import java.io.*;

public class Boggle {

    private static volatile boolean isTimerActive = true;

    public static void main(String[] args) {
        
        
        try {
            Scanner in = new Scanner(System.in);

            System.out.print("How many players?: ");
            int numberOfPlayers = in.nextInt();

            GameManager game = new GameManager();
            GameDictionary dictionary = new GameDictionary();
            dictionary.loadDictionary("words.txt");

            game.newGame(4, numberOfPlayers, "cubes.txt", dictionary);

            int totalPlayers = numberOfPlayers;
            while (numberOfPlayers-- > 0) {

                Timer timer = new Timer();
                isTimerActive = true;

                TimerTask task = new TimerTask() {
                    int countdown = 15;
                    @Override
                    public void run() {
                        if (countdown > 0) {
                            if (countdown == 5){
                                System.out.println("5 seconds left");
                            }
                            
                            countdown--;
                        } else {
                            timer.cancel();
                            isTimerActive = false;
                        }
                    }
                };

                timer.scheduleAtFixedRate(task, 0, 1000);

                while (isTimerActive) {
                    char[][] board = game.getBoard();
                    for (char[] c : board) {
                        System.out.println(Arrays.toString(c));
                    }

                    game.uncapitalize();

                    System.out.print("Player " + numberOfPlayers + " guesses: ");
                    String input = in.next();

                    int score = game.addWord(input, numberOfPlayers);

                    clearConsole();
                    if (score > 0) {
                        System.out.println(score + " points added");
                    }
                    else System.out.println("invalid word");

                    game.capitalize();
                }

                game.uncapitalize();
            }

            for (int j = 0; j < totalPlayers; j++) {
                    System.out.println("Player " + j + ": " + game.getScores()[j]);
                    System.out.println("Words: " + game.getWords(j));
                    System.out.println();
                }

            System.out.println("\n\n");
            game.uncapitalize();
            Collection<String> answers = game.getAllWords();
            int compScore = game.getComputerScore();
            System.out.println("Other possible words: " + answers);
            System.out.println("Computer score: " + compScore);

            in.close();
        }
        catch (IOException e) {

        }
        
    }

    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

}


