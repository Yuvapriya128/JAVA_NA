package producerConsumer;

import java.util.LinkedList;
import java.util.Queue;

public class Inventory {
    private Queue<String> stock=new LinkedList<>();

//    wait -> needs throws InterruptedException
//    add method-> producer
//    remove method-> consumer

    public String add(String item) throws InterruptedException {

        stock.add(item);
        return item;
//        added item is returned
    }

    public String remove() throws InterruptedException {
        return stock.remove();
    }

    public int size(){
        return stock.size();
    }
}
