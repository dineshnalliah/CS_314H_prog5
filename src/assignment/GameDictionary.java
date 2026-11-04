package assignment;

import java.io.*;
import java.util.*;

public class GameDictionary implements BoggleDictionary {

    private Trie dictionary;
    private boolean isLoaded;

    @Override
    // Tests whether a String is a word in this dictionary.
    public boolean contains(String word) {
        if (!isLoaded) return false;
        return dictionary.search(word);
    }

    @Override
    // Tests whether a String is a word in this dictionary.
    public boolean isPrefix(String prefix) {
        if (!isLoaded) return false;
        return dictionary.startsWith(prefix);
    }

    @Override
    // Adds words from a file to this dictionary. The file should contain one word per line, with the words in ascending lexicographic order.
    public void loadDictionary(String filename) throws IOException {
        Trie newDictionary = new Trie();
        // a missing file throws FileNotFoundException (an IOException) to the caller
        try (Scanner in = new Scanner(new File(filename))) {
            while (in.hasNext()) {
                String word = in.next().toLowerCase();
                if (word.matches("[a-z]+")) newDictionary.insert(word);
            }
        }
        dictionary = newDictionary;
        isLoaded = true;
    }

    @Override
    public Iterator<String> iterator() {
        if (!isLoaded) return new DictIterator(null);
        return new DictIterator(dictionary.getRoot());
    }

    private class DictIterator implements Iterator<String> {
        private class IterState {
            final Node node;
            final String word;
            int childIndex;

            IterState(Node node, String word) {
                this.node = node;
                this.word = word;
                this.childIndex = 0; 
            }
        }

        private final Stack<IterState> stack = new Stack<>();
        private String nextWord = null;

        public DictIterator(Node root) {
            if (root != null) {
                stack.push(new IterState(root, ""));
                advance(); // Pre-fetch the first word
            }
        }

        private void advance() {
            nextWord = null;

            while (!stack.isEmpty()) {
                IterState current = stack.peek();

                // if the current node we're on is the end of a node
                if (current.childIndex == 0 && current.node.isEndOfWord && nextWord == null) {
                    nextWord = current.word;
                }

                boolean foundChild = false;
                while (current.childIndex < 26) {
                    int index = current.childIndex;
                    current.childIndex++; // Move the pointer forward for the next visit
                    
                    if (current.node.children[index] != null) {
                        Node childNode = current.node.children[index];
                        char childChar = (char) ('a' + index);
                        
                        // Push the child state onto the stack
                        stack.push(new IterState(childNode, current.word + childChar));
                        foundChild = true;
                        break;
                    }
                }

                if (foundChild) {
                    if (nextWord != null) return;
                    continue;
                }

                stack.pop();

                if (nextWord != null) {
                    return;
                }
            }
        }


        // Check if there are elements left to traverse
        @Override
        public boolean hasNext() {
            return nextWord != null;
        }

        // Return the current element and advance the cursor
        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String s = nextWord;
            advance();
            return s;
        }
    }

}
