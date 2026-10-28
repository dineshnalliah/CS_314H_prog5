package assignment;


class Node {
    Node[] children;
    boolean isEndOfWord;

    public Node() {
        children = new Node[26];
    }

}

public class Trie {
    
    private Node root;

    public Trie() {
        root = new Node();
    }

    public void insert(String word) {

        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (current.children[c - 'a'] == null) {
                current.children[c - 'a'] = new Node();
            }
            current = current.children[c - 'a'];
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            // for this fix and the bottom fix, prevents game from crashing if odd input from player
            // if(c < 'a' || c > 'z') return false;

            if (current.children[c - 'a'] == null) {
                return false;
            }
            current = current.children[c - 'a'];
        }
        return current != null && current.isEndOfWord; 
    }
 
    public boolean startsWith(String prefix) {
        Node current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);

            // add this
            // if(c < 'a' || c > 'z') return false;

            if (current.children[c - 'a'] == null) {
                return false;
            }
            current = current.children[c - 'a'];
        }

        return true;
    }

    public Node getRoot() {
        return root;
    }

}
