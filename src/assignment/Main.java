package assignment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        try {
            GameManager x = new GameManager();
            GameDictionary dictionary = new GameDictionary();
            dictionary.loadDictionary("words.txt");
            x.newGame(4, 0, "cubes.txt", dictionary);
            x.setGame(x.test);

            char[][] b = x.getBoard();

            System.out.println(dictionary.isPrefix("k"));

            for (char[] c : b) {
                System.out.println(Arrays.toString(c));
            }

            Collection<String> answers = x.getAllWords();

            System.out.println(answers);

            
        }
        catch (IOException e) {
            System.out.println("input failed");
        }
        

        


       
    }
}
