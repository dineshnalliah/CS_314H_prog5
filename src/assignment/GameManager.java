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
        // add this: null check for word, player typing capitals would be marked wrong, addWord never checks dictionary
        // also return 0 since interface says addWord returns points earned and a repeated word returns 0 which other people would think is the expected output
        if (word == null) return 0;
        word = word.toLowerCase();
        if (word.length() < 4) return 0;
        if (!dict.contains(word)) return 0;
        if (playerWords[player].contains(word)) return 0;

        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                boolean[][] visited = new boolean[board.length][board.length];
                List<Point> possiblePath = new ArrayList<Point>();
                if (isValid(i, j, word, visited, possiblePath) ) {
                    count = word.length()-3;
                    this.path = possiblePath;
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
        // these fields are never cleared so calling getAllWords() twice would double the score
        words = new HashSet<>();
        computerScore = 0;
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

        // prevents double counting if a word can be found two ways on the board
        if (dict.contains(word) && word.length() >= 4 && !foundByPlayer(word)) {
            if (words.add(word)) computerScore += word.length() - 3;
        }

        if (!dict.isPrefix(word)) return;

        visited[i][j] = true;
        for (int d = 0; d < 8; d++) {
            boardDrivenFind(i + ROW_OFFSETS[d], j + COL_OFFSETS[d], words, word, visited);
        }
        visited[i][j] = false; 
    }

    private void dictionaryDrivenFind() {
        for (String x : dict) {
            if (x.length() < 4 || foundByPlayer(x)) continue;
            boolean found = false;
            for (int i = 0; i < board.length && !found; i++) {
                for (int j = 0; j < board.length && !found; j++) {
                    boolean[][] visited = new boolean[board.length][board.length];
                    if (isValid(i, j, x, visited, new ArrayList<Point>())) {
                        words.add(x);
                        computerScore += x.length() - 3;
                        found = true;
                    }
                }
            }
        }
    }

    private boolean foundByPlayer(String word) {
        for (int x = 0; x < numPlayers; x++) {
            if (playerWords[x].contains(word)) return true;
        }
        return false;
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
        List<String> cubes = new ArrayList<>();
        Scanner scan = new Scanner(new File(cubeFile));
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim().toLowerCase();
            if (!line.isEmpty()) cubes.add(line);
        }
        scan.close();
        Collections.shuffle(cubes);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String s = cubes.get(i * size + j);
                board[i][j] = s.charAt((int)(Math.random() * s.length()));
            }
        }

        this.numPlayers = numPlayers;
        this.dict = dict;
        this.words = new HashSet<>();
        this.path = new ArrayList<Point>(); 
        this.path = null;
        this.playerWords = new Set[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            this.playerWords[i] = new HashSet<String>();
        }
        this.playerScores = new int[this.numPlayers];
        this.tactic = true;
        this.computerScore = 0;
    }

    public void resetPath() {
        //path = new ArrayList<>();
        path = null;
    }

    @Override
    public void setGame(char[][] board)  {
        this.board = board;
        for (int i = 0; i < numPlayers; i++) {
            playerWords[i] = new HashSet<>();
        }
        playerScores = new int[numPlayers];
        path = null;
        words = new HashSet<>();
        computerScore = 0;
    }

    @Override
    public void setSearchTactic(SearchTactic tactic) {
        if(tactic == null) tactic = SEARCH_DEFAULT;
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
