package assignment;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class GameManager implements BoggleGame {

    private char[][] board;
    private int numPlayers;
    private String cubeFile;
    private BoggleDictionary dict;

    @Override
    public int addWord(String word, int player) {
        // TODO - implement me!
        return 0;
    }

    @Override
    public Collection<String> getAllWords() {
        // TODO - implement me!
        return null;
    }

    @Override
    public char[][] getBoard() {
        try {
            Scanner scan = new Scanner(new File("cubes.txt"));
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    String s = scan.nextLine();
                    System.out.println(s);
                    int index = (int)(Math.random() * 6);
                    board[i][j] = s.charAt(index);
                    System.out.println(board[i][j]);
                }
            }
             scan.close();
        }
        catch (FileNotFoundException e) {

        }
        return board;
    }

    @Override
    public List<Point> getLastAddedWord() {
        // TODO - implement me!
        return null;
    }

    @Override
    public int[] getScores() {
        // TODO - implement me!
        return null;
    }

    @Override
    public void newGame(int size, int numPlayers, String cubeFile, BoggleDictionary dict) throws IOException {
        board = new char[size][size];
        try {
            Scanner scan = new Scanner(new File(cubeFile));
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    String s = scan.nextLine();
                    System.out.println(s);
                    int index = (int)(Math.random() * 6);
                    board[i][j] = s.charAt(index);
                    System.out.println(board[i][j]);
                }
            }
            scan.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        this.numPlayers = numPlayers;
        this.cubeFile = cubeFile;
        this.dict = dict;
    }

    @Override
    public void setGame(char[][] board)  {
        this.board = board;
       
    }

    @Override
    public void setSearchTactic(SearchTactic tactic) {
        // TODO - implement me!

    }

}
