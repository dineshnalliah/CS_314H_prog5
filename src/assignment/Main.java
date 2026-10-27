package assignment;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            int countdown = 60;

            @Override
            public void run() {
                if (countdown > 0) {
                    System.out.println("Time remaining: " + countdown + " seconds");
                    countdown--;
                } else {
                    timer.cancel();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);

        // try {
        //     GameManager x = new GameManager();
        //     GameDictionary dictionary = new GameDictionary();
        //     dictionary.loadDictionary("words.txt");
        //     x.newGame(4, 0, "cubes.txt", dictionary);
        //     x.setGame(x.test);

        //     x.tactic = true;

        //     char[][] b = x.getBoard();

        //     for (char[] c : b) {
        //         System.out.println(Arrays.toString(c));
        //     }

        //     Collection<String> answers = x.getAllWords();

        //     System.out.println(answers);
        //     System.out.println(answers.size());

        //     System.out.println(x.addWord("kites", 1));
        //     x.capitalize();

        //     for (char[] c : b) {
        //         System.out.println(Arrays.toString(c));
        //     }

        //     System.out.println(x.addWord("kiwi", 1));
        //     x.capitalize();

        //     for (char[] c : b) {
        //         System.out.println(Arrays.toString(c));
        //     }

        // }
        // catch (IOException e) {
        //     System.out.println("input failed");
        // }
        

        


       
    }
}
