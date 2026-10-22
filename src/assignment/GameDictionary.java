package assignment;

import java.io.FileNotFoundException;
import java.io.*;
import java.util.*;

public class GameDictionary implements BoggleDictionary {

    private Trie dictionary;

    @Override
    public boolean contains(String word) {
        return dictionary.search(word);
    }

    @Override
    public boolean isPrefix(String prefix) {
        return dictionary.startsWith(prefix);
    }

    @Override
    public void loadDictionary(String filename) throws IOException {
        dictionary = new Trie();
        try {
            Scanner in = new Scanner(new File(filename));
            while (in.hasNext()) {
                dictionary.insert(in.next().toLowerCase());
            }
            in.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("file not found");
        }

    }

    @Override
    public Iterator<String> iterator() {
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
