package SC.datastructures;

/**
 * A pair of ints to represent the start and end indices of tokens
 */
public class IndexPair {
    private final int begin;
    private final int end;

    public IndexPair(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public String toString() {
        return getBegin() + "," + getEnd();
    }
}
