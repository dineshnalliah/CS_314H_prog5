package assignment;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class GameManager implements BoggleGame {

    private char[][] board;
    private int numPlayers;
    private BoggleDictionary dict;
    private Set<String> words;
    private Set<String> computerWords;
    private int computerScore;
    private boolean tactic;
    private List<Point> path;
    private Set<String>[] playerWords;
    private int[] playerScores;

    private final int[] ROW_OFFSETS = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] COL_OFFSETS = {-1, 0, 1, -1, 1, -1, 0, 1};


    @Override
    // Adds a word to the player's list and returns the point value of the word. 
    // If the word is invalid or the player cannot add the word, it is worth zero points and is not actually added to the player's list. 
    // Player should not be able to add the same word multiple times.
    public int addWord(String word, int player) {
        if (word == null || player < 0 || player >= numPlayers) return 0;
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
    // Returns a collection containing all valid words in the current Boggle board. Uses the current search tactic.
    public Collection<String> getAllWords() {
        words = new HashSet<>();
        computerWords = new HashSet<>();
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

    // Checks if the given word is found on the board
    private void boardDrivenFind(int i, int j, Set<String> words, String word, boolean[][] visited) {

        if (i < 0 || i >= board.length || j < 0 || j >= board.length) return;
        if (visited[i][j]) return;

        word += board[i][j];

        // prevents double counting if a word can be found two ways on the board
        if (dict.contains(word) && word.length() >= 4 && words.add(word)) {
            // words.add(word);
            if (!foundByPlayer(word)) { 
                computerScore += word.length() - 3;
                computerWords.add(word);
            }
            
        }

        if (!dict.isPrefix(word)) return;

        visited[i][j] = true;
        for (int d = 0; d < 8; d++) {
            boardDrivenFind(i + ROW_OFFSETS[d], j + COL_OFFSETS[d], words, word, visited);
        }
        visited[i][j] = false; 
    }

    // Checks if any word in the dictionary is located on the board
    private void dictionaryDrivenFind() {
        for (String x : dict) {
            if (x.length() < 4) continue;
            if (foundByPlayer(x)) {
                words.add(x);
                continue;
            }
            boolean found = false;
            for (int i = 0; i < board.length && !found; i++) {
                for (int j = 0; j < board.length && !found; j++) {
                    boolean[][] visited = new boolean[board.length][board.length];
                    if (isValid(i, j, x, visited, new ArrayList<Point>())) {
                        words.add(x);
                        computerWords.add(x);
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

    public Set<String> getComputerWords() {
        return computerWords;
    }

    public Set<String> getWords(int player) {
        return playerWords[player];
    }


    @Override
    @SuppressWarnings ("unchecked")
    // Creates a new Boggle game using a size x size board and the cubes specified in the file cubeFile.
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
        this.path = null;
        this.playerWords = new Set[numPlayers];
        this.computerWords = new HashSet<>();
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
    // Sets the game board to the given board (also in row-major order, sets all player scores and lists to zero/empty. 
    // Very useful for debugging or playing a previous game. The board must be square. 
    // Other game-related parameters (like the dictionary) should be left as is.
    public void setGame(char[][] board)  {
        // copy so later changes to the caller's array can't change our board
        char[][] copy = new char[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                copy[i][j] = Character.toLowerCase(board[i][j]);
            }
        }
        this.board = copy;
        for (int i = 0; i < numPlayers; i++) {
            playerWords[i] = new HashSet<>();
        }
        playerScores = new int[numPlayers];
        path = null;
        words = new HashSet<>();
        computerScore = 0;
        computerWords = new HashSet<>();
    }

    @Override
    // Sets the search tactic (used by getAllWords()) to the given tactic. Valid tactics are defined earlier. 
    // The default tactic should be SEARCH_DEFAULT. An invalid tactic resets to default.
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
