package executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorIntro {
    public static void main(String[] args) {

//        Fixed Thread Pool
        System.out.println("Fixed thread pool");
        ExecutorService executor= Executors.newFixedThreadPool(3);

        for(int i=1;i<=10;i++){
            int task_id=i;
            executor.execute(()->{
                System.out.println(task_id+" executed by Thread "+Thread.currentThread().getName());
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Single thread executor");
        ExecutorService executor2= Executors.newSingleThreadExecutor();

        for(int i=1;i<=10;i++){
            int task_id=i;
            executor2.execute(()->{
                System.out.println(task_id+" executed by Thread "+Thread.currentThread().getName());
            });
        }

        executor2.shutdown();
        try {
            executor2.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Cached thread pool");
        ExecutorService executor3= Executors.newCachedThreadPool();

        for(int i=1;i<=10;i++){
            int task_id=i;
            executor3.execute(()->{
                System.out.println(task_id+" executed by Thread "+Thread.currentThread().getName());
            });
        }

        executor3.shutdown();
        try {
            executor3.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



    }
}
