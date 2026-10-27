package assignment;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        long targetTime = System.currentTimeMillis() + 10_000; // 10 seconds from now

        while (System.currentTimeMillis() < targetTime) {
            // Check if we are there yet
            System.out.println("Are we there yet? Not yet.");

            try {
                // Sleep for 500ms to yield the CPU
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                break;
            }
        }

        try {
            GameManager x = new GameManager();
            GameDictionary dictionary = new GameDictionary();
            dictionary.loadDictionary("words.txt");
            x.newGame(4, 0, "cubes.txt", dictionary);
            x.setGame(x.test);

            x.setSearchTactic(BoggleGame.SearchTactic.SEARCH_BOARD);

            char[][] b = x.getBoard();

            for (char[] c : b) {
                System.out.println(Arrays.toString(c));
            }

            Collection<String> answers = x.getAllWords();

            System.out.println(answers);
            System.out.println(answers.size());

            // System.out.println(x.addWord("kites", 1));
            // x.capitalize();

            // for (char[] c : b) {
            //     System.out.println(Arrays.toString(c));
            // }

            // System.out.println(x.addWord("kiwi", 1));
            // x.capitalize();

            // for (char[] c : b) {
            //     System.out.println(Arrays.toString(c));
            // }

        }
        catch (IOException e) {
            System.out.println("input failed");
        }
        

        


       
    }
}
