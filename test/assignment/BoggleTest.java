package assignment;
 
import static org.junit.jupiter.api.Assertions.*;
 
import java.awt.Point;
import java.io.IOException;
import java.util.*;
 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* 
 * Any comments and methods here are purely descriptions or suggestions.
 * This is your test file. Feel free to change this as much as you want.
 */

public class BoggleTest {

    private static final String WORDS_FILE = "words.txt";
    private static final String CUBES_FILE = "cubes.txt";
 
    private static final int WORD_COUNT = 113764;  // number of words in words.txt
 
    private static BoggleDictionary dict;
    private BoggleGame game;

    private static final Set<String> ALL_WORDS_ON_BOARD = Set.of(
    "ates", "awes", "iwis", "kite", "kites", "kiwi", "kiwis", "pies",
    "pieta", "piety", "pika", "pita", "pity", "seta", "tews", "tipi",
    "tipis", "vita", "wait", "wise", "wite", "wites", "wyte", "wytes");

    private static char[][] makeBoard() {
        return new char[][] {
            {'y', 'w', 'e', 's'},
            {'a', 't', 'w', 'i'},
            {'k', 'i', 'p', 'i'},
            {'v', 'z', 'q', 'j'}
        };
    }

    // This will run ONCE before all other tests. It can be useful to setup up
    // global variables and anything needed for all of the tests.
    @BeforeAll
    static void setupAll() throws IOException {
        dict = new GameDictionary();
        dict.loadDictionary(WORDS_FILE);
    }

    // This will run before EACH test.
    @BeforeEach
    void setupEach() throws IOException {
        game = new GameManager();
        game.newGame(4, 2, CUBES_FILE, dict);
        game.setGame(makeBoard());
    }

    // You can test Boggle here. You will want to make additional tests as well
    @Test
    void testBoggle() {

    }

    // You can test GameManager here. You will want to make additional tests as well
    @Test
    void testAddWordValidWords() {
        assertEquals(1, game.addWord("kite", 0));   // 4 letters = 1 point
        assertEquals(2, game.addWord("kites", 0));  // 5 letters = 2 points
        assertEquals(2, game.addWord("pieta", 1));
 
        int[] scores = game.getScores();
        assertEquals(2, scores.length);  // one score per player
        assertEquals(3, scores[0]);
        assertEquals(2, scores[1]);
    }
 
    @Test
    void testAddWordInvalidWords() {
        assertEquals(0, game.addWord("", 0));       // empty string
        assertEquals(0, game.addWord("pie", 0));    // on board and in dictionary, but too short
        assertEquals(0, game.addWord("zebra", 0));  // in dictionary, but not on the board
        assertEquals(0, game.addWord("kitw", 0));   // on the board, but not in the dictionary
        assertEquals(0, game.addWord("twit", 0));   // would need to reuse the only t
 
        assertEquals(0, game.getScores()[0]);       // none of these should change the score
    }
 
    @Test
    void testAddWordDuplicate() {
        assertEquals(1, game.addWord("kite", 0));
        assertEquals(0, game.addWord("kite", 0));   // same player, same word: no points
        assertEquals(1, game.getScores()[0]);       // score is unchanged
 
        assertEquals(1, game.addWord("kite", 1));   // a different player can still add it
    }
 
    @Test
    void testGetLastAddedWord() {
        assertNull(game.getLastAddedWord());  // spec: null if no word has been added yet
 
        game.addWord("kite", 0);
        // Points are (row, column), one per letter, in order
        List<Point> expected = Arrays.asList(
            new Point(2, 0), new Point(2, 1), new Point(1, 1), new Point(0, 2));
        assertEquals(expected, game.getLastAddedWord());
 
        game.addWord("zebra", 0);  // invalid word should not change the last path
        assertEquals(expected, game.getLastAddedWord());
    }
 
    @Test
    void testGetAllWordsBothTactics() {
        game.setSearchTactic(BoggleGame.SearchTactic.SEARCH_BOARD);
        assertEquals(ALL_WORDS_ON_BOARD, new HashSet<>(game.getAllWords()));
 
        game.setSearchTactic(BoggleGame.SearchTactic.SEARCH_DICT);
        assertEquals(ALL_WORDS_ON_BOARD, new HashSet<>(game.getAllWords()));
 
        // Edge case: a smaller (2x2) board. Results should reflect only the new board.
        //   t o
        //   e n
        game.setGame(new char[][] {{'t', 'o'}, {'e', 'n'}});
        game.setSearchTactic(BoggleGame.SearchTactic.SEARCH_BOARD);
        assertEquals(Set.of("note", "tone"), new HashSet<>(game.getAllWords()));
    }
 
    @Test
    void testSetGameResetsGame() {
        game.addWord("kite", 0);
        game.addWord("kites", 1);
 
        game.setGame(makeBoard());
 
        // spec: setGame resets all scores to zero and all word lists to empty
        assertArrayEquals(new int[] {0, 0}, game.getScores());
        assertArrayEquals(makeBoard(), game.getBoard());
        assertEquals(1, game.addWord("kite", 0));  // allowed again, since the list was emptied
    }
    
    @Test
    void testGameManager() {
        
    }

    // You can test GameDictionary here. You will want to make additional tests as well
    @Test
    void testDictionaryContains() {
        assertTrue(dict.contains("kite"));
        assertTrue(dict.contains("aa"));        // first word in words.txt
        assertTrue(dict.contains("zymurgy"));   // last word in words.txt (no newline after it)
        assertFalse(dict.contains("aardvar"));  // only a prefix of "aardvark", not a word
        assertFalse(dict.contains("kitw"));     // not a word
        assertFalse(dict.contains(""));         // empty string is not a word
    }
 
    @Test
    void testDictionaryIsPrefix() {
        assertTrue(dict.isPrefix("a"));
        assertTrue(dict.isPrefix("aardvar"));
        assertTrue(dict.isPrefix("aardvark"));   // a full word counts as its own prefix
        assertTrue(dict.isPrefix("zymurgy"));    // last word
        assertFalse(dict.isPrefix("zymurgyy"));  // goes past the end of the last word
        assertFalse(dict.isPrefix("xq"));        // no word starts with "xq"
    }
 
    @Test
    void testDictionaryIteratorAllWordsInOrder() {
        int count = 0;
        String previous = null;
        for (String word : dict) {
            if (previous != null) {
                assertTrue(previous.compareTo(word) < 0, previous + " came before " + word);
            }
            assertTrue(dict.contains(word));
            previous = word;
            count++;
        }
        assertEquals(WORD_COUNT, count);
        assertEquals("zymurgy", previous);
    }
 
    @Test
    void testDictionaryIteratorEdgeCases() {
        Iterator<String> it1 = dict.iterator();
        Iterator<String> it2 = dict.iterator();
 
        assertTrue(it1.hasNext());
        assertTrue(it1.hasNext());
        assertEquals("aa", it1.next());
        assertEquals("aah", it1.next());
 
        assertEquals("aa", it2.next());
 
        while (it1.hasNext()) {
            it1.next();
        }
        assertThrows(NoSuchElementException.class, it1::next);
    }
}
