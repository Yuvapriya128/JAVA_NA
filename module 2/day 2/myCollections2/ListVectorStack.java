package MyCollections2;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class ListVectorStack {
    public static void main(String[] args) {
//        vector works the same as ArrayList
        List<Integer> listvector=new Vector<>();
        listvector.add(1);
        listvector.add(4);
        listvector.add(3);
        System.out.println(listvector);
        listvector.remove(0);
        System.out.println(listvector);

//
//        List<Integer> stacklist=new Stack<>();
//        stacklist.push(1);  not working



        Stack<Integer> stacklist=new Stack<>();
        stacklist.push(1);

    }
}
