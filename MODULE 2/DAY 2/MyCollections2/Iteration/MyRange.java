package MyCollections2.Iteration;

import java.util.Iterator;
//Iterable is in java.lang.*

public class MyRange implements Iterable<Integer>{
    private int start;
    private int end;

    public MyRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

//    covariant
    @Override
    public Iterator iterator() {
        return new MyRangeIterator(start,end);
    }
}
