package assignment;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class GameManager implements BoggleGame {

    private char[][] board;
    private int numPlayers;
    private BoggleDictionary dict;
    private Set<String> words;

    public final char[][] test = {
        {'y', 'w', 'e', 's'},
        {'a', 't', 'w', 'i'},
        {'k', 'i', 'p' ,'i'},
        {'v', 'z', 'q', 'j'}
    };

    private final int[] ROW_OFFSETS = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] COL_OFFSETS = {-1, 0, 1, -1, 1, -1, 0, 1};


    @Override
    public int addWord(String word, int player) {
        // TODO - implement me!
        return 0;
    }

    @Override
    public Collection<String> getAllWords() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                boolean[][] visited = new boolean[board.length][board.length];
                findAll(i, j, words, "", visited);
            }
        }
        return words;
    }



    private void findAll(int i, int j, Set<String> words, String word, boolean[][] visited) {

        if (i < 0 || i >= board.length || j < 0 || j >= board.length) return;
        if (visited[i][j]) return;

        word += board[i][j];

        if (dict.contains(word) && word.length() >= 4) words.add(word);
        if (!dict.isPrefix(word)) return;

        visited[i][j] = true;
        for (int d = 0; d < 8; d++) {
            findAll(i + ROW_OFFSETS[d], j + COL_OFFSETS[d], words, word, visited);
        }
        visited[i][j] = false; // <- we were missing this :( 
        /* lets take the case where we start at y in the top left, and it searches right first to get yw, now w has been marked
           visited. This is fine for this depth, if we look at the instance where y goes to ya first and then checks yaw, it 
           registers yaw as not valid because w was visited. essentially the issue is that the for a search starting at some node, 
           every character can only be used once for every possible path combined, which is problematic */

        /* there is a nuance between a dfs algorithm and a backtracking algorithm, i realized this after googling a general
           recursive find all paths algorithm. if you're curious, search up dfs vs backtracking on google or ai
           
           thus, i changed the name of the method to findAll rather than dfs*/ 
    }

    @Override
    public char[][] getBoard() {
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
        this.board = new char[size][size];
        Scanner scan = new Scanner(new File(cubeFile));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String s = scan.nextLine().toLowerCase();
                int index = (int)(Math.random() * 6);
                board[i][j] = s.charAt(index);
            }
        }
        scan.close();

        this.numPlayers = numPlayers;
        this.dict = dict;
        this.words = new HashSet<>();
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
