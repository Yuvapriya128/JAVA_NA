package MyCollections2.Iteration;

import java.util.Iterator;

public class MainMyRange {
    public static void main(String[] args) {
        MyRange mr=new MyRange(10,20);
        Iterator itr=mr.iterator();
        System.out.println(itr.getClass().getSimpleName());
        while(itr.hasNext()){
            System.out.println(itr.next());
        }

        System.out.println("_____________");
//        next is returning object
//        Downcast : Tell object that it's an Integer also
//        for(Object data:mr){
//            System.out.println((Integer)data);
//        }

//        If you use GENERICS <> in Iterable & Iterator, then Integer i can use
        for(Integer data:mr){
            System.out.println(data);
        }
    }
}
