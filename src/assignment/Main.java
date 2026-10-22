package assignment;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        GameManager x = new GameManager();
        char[][] b = x.getBoard();

        for (char[] c : b) {
            System.out.println(Arrays.toString(c));
        }
    }
}
