package callable;

import java.util.concurrent.Callable;

public class MyCallable implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        Thread.sleep((int)(Math.random()*10));
        System.out.println("Thread called by "+Thread.currentThread().getName());
        return (int)(1000*(Math.random()*10));
    }
}
