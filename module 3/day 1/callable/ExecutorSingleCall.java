package callable;

import java.util.concurrent.*;

public class ExecutorSingleCall {
    public static void main(String[] args) {
        ExecutorService executorService= Executors.newSingleThreadExecutor();

        Future<Integer> futureval=executorService.submit(new MyCallable());

        int val= 0;
        try {
            val = futureval.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted "+e.getMessage());
        } catch (ExecutionException e) {
            System.out.println("Execution exception "+e.getMessage());
        } catch (TimeoutException e) {
            System.out.println("TimeOut exception "+e.getMessage());
        }
        System.out.println("Future integer value "+val);

        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Lamda function");
        ExecutorService executorService1=Executors.newSingleThreadExecutor();
        Future<String> futureString=executorService1.submit(()->{
            System.out.println("Thread called by"+Thread.currentThread().getName());
            return "Nature";
        });
        String fval=null;

        try {
            fval = futureString.get(5, TimeUnit.SECONDS);
            System.out.println("Future String value: "+fval);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted "+e.getMessage());
        } catch (ExecutionException e) {
            System.out.println("Execution exception "+e.getMessage());
        } catch (TimeoutException e) {
            System.out.println("TimeOut exception "+e.getMessage());
        }

        executorService1.shutdown();
    }
}
