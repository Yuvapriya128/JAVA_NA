package Thread;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/*
* When you run inside IntelliJ, the JVM starts extra background threads for IDE support.
These may include:

✅ Reference Handler thread
✅ Finalizer thread
✅ Signal Dispatcher
✅ IDE debugging / monitoring threads
*
* Thread.activeCount() returns:

Approximate number of active threads in the current thread group

So:

It’s not exact
It depends on environment (CMD vs IDE vs Debug mode)
*
* */
public class MyThread extends Thread {

//    default constructor will work

    //    The below is constructor chaining
    private Writer w;
    public MyThread(String name,Writer w) {
        super(name);
        this.w=w;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            try {
                w.write(i + " " +this.getName()+"\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args)  {
        try {
            Writer w=new FileWriter("Demo.txt");

        Thread t1=new MyThread("Sachin",w);
        Thread t2=new MyThread("Yuva",w);
        t1.start();
        t2.start();

        w.write("Active threads: "+Thread.activeCount()+"\n");

//        Thread runs parallely so output will get both no order

//        thread joining parent thread will wait for child thread to finish
//        its execution
//        thread joining throws checked exception -> use try/catch or throws InterruptedException


/*
* if i do t2.join then main thread will wait for only t2 to finish then it will start
*
*
* */
            try {
//            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Message "+e.getMessage());
        }


        for (int i=1;i<=100;i++){
            w.write(i+" "+Thread.currentThread().getName()+"\n");
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Active threads: "+Thread.activeCount());
    }



}
