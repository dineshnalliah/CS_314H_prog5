package assignment;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class GameManager implements BoggleGame {

    private char[][] board;
    private int numPlayers;
    private BoggleDictionary dict;
    private Set<String> words;
    private int computerScore;
    private boolean tactic;
    private List<Point> path;
    private Set<String>[] playerWords;
    private int[] playerScores;

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
        if (word.length() < 4) return 0;
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                boolean[][] visited = new boolean[board.length][board.length];
                List<Point> possiblePath = new ArrayList<Point>();
                if (isValid(i, j, word, visited, possiblePath)) {
                    count = word.length();
                    playerWords[player].add(word);
                    playerScores[player] += count;
                    break;
                }
            }
            if (count > 0) break;
        }

        return count;
    }

    // substring not very efficient, maybe opt for some other approach
    private boolean isValid(int i, int j, String s, boolean[][] visited, List<Point> path) {
        if (i < 0 || i >= board.length || j < 0 || j >= board.length) return false;
        if (visited[i][j]) return false;
        if (s.charAt(0) != board[i][j]) return false;
        if (s.length() == 1 && board[i][j] == (s.charAt(0))) { 
            path.add(new Point(i, j));
            this.path = path;
            return true;
        }
        visited[i][j] = true;
        path.add(new Point(i, j));
        for (int d = 0; d < 8; d++) {
            if(isValid(i + ROW_OFFSETS[d], j + COL_OFFSETS[d], s.substring(1), visited, path)) {
                return true;
            }
        }
        path.remove(path.size()-1);
        visited[i][j] = false;
        return false;
    }

    @Override
    // fix this
    public Collection<String> getAllWords() {
        boolean[][] visited = new boolean[board.length][board.length];
        if(tactic) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    boardDrivenFind(i, j, words, "", visited);
                }
            }
        } else {
            dictionaryDrivenFind();
        }
        return words;
    }

    private void boardDrivenFind(int i, int j, Set<String> words, String word, boolean[][] visited) {

        if (i < 0 || i >= board.length || j < 0 || j >= board.length) return;
        if (visited[i][j]) return;
        

        word += board[i][j];

        if (dict.contains(word) && word.length() >= 4) {
            for (int ind = 0; ind < numPlayers; ind++) {
                if (playerWords[ind].contains(word)) return;
            }
            computerScore += word.length();
            words.add(word);
        }
        if (!dict.isPrefix(word)) return;

        visited[i][j] = true;
        for (int d = 0; d < 8; d++) {
            boardDrivenFind(i + ROW_OFFSETS[d], j + COL_OFFSETS[d], words, word, visited);
        }
        visited[i][j] = false; 
    }

    private void dictionaryDrivenFind() {
        //boolean[][] visited = new boolean[board.length][board.length];
        for (String x : dict) {
            if (x.length() < 4) continue;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    boolean[][] visited = new boolean[board.length][board.length];
                    if (isValid(i, j, x, visited, new ArrayList<Point>())) {
                        words.add(x);
                    }
                }
            }
        }
    }

    @Override
    public char[][] getBoard() {
        return board;
    }

    @Override
    public List<Point> getLastAddedWord() {
        return path;
    }

    @Override
    public int[] getScores() {
        return playerScores;
    }

    public int getComputerScore() {
        return computerScore;
    }

    public Set<String> getWords(int player) {
        return playerWords[player];
    }


    @Override
    @SuppressWarnings ("unchecked")
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
        this.path = new ArrayList<Point>();
        this.playerWords = new Set[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            this.playerWords[i] = new HashSet<String>();
        }
        this.playerScores = new int[this.numPlayers];
        this.tactic = true;
        this.computerScore = 0;
    }

    public void resetPath() {
        path = new ArrayList<>();
    }

    @Override
    public void setGame(char[][] board)  {
        this.board = board;
       
    }

    @Override
    public void setSearchTactic(SearchTactic tactic) {
       switch(tactic) {
            case SEARCH_BOARD:
                this.tactic = true;
                break;
            case SEARCH_DICT:
                this.tactic = false;
                break;
       }
    }
}
