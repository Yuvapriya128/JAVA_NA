package deadlock;

import java.awt.print.PrinterJob;

public class DeadlockRes {
    public static void main(String[] args) {
        class Philosopher implements Runnable{
//            if resource shared -> make it static

            private static Object chopstick1=new Object();
            private static Object chopstick2=new Object();
            private String name;

//            no constructor chaining -> uses interface

            public Philosopher(String name){
                this.name=name;
            }
            @Override
            public void run(){
                System.out.println(name+" is occupying chopstick1");
                synchronized (chopstick1){
                    System.out.println(this.name +" occupied chopstick1 going for chopstick2");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (chopstick2){
                        System.out.println(this.name+" occupied chopstick2 now eating...");
                    }
                }

            }
        }

        Thread t1=new Thread(new Philosopher("Philosopher1"));
        Thread t2=new Thread(new Philosopher("Philosopher2"));
        t1.start();
        t2.start();
    }
}
