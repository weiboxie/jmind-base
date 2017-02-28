package jmind.base.match.ac;

public class TrieNode {

    private int nextState;
    private char nextChar;

    public int getNextState() {
        return nextState;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
    }

    public char getNextChar() {
        return nextChar;
    }

    public void setNextChar(char nextChar) {
        this.nextChar = nextChar;
    }

    @Override
    public String toString() {

        return "+" + nextChar + "=" + nextState;
    }
}
