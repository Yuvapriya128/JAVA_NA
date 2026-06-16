package Thread.runnable;

public class Main {
    public static void main(String[] args) {
        Thread t1=new Thread(new MyRunnable());
        Thread t2=new Thread(new MyRunnable());
        Thread t3=new Thread(new MyRunnable());

        t1.start();
        t2.start();
        t3.start();

        System.out.println("Exiting Main method");

        Runnable r=()->{
            System.out.println("This is thread 4");
            for(int i=1;i<=10;i++)
            System.out.println(i+" "+Thread.currentThread().getName()+"\n");
        };
        Thread t4=new Thread(r);
        t4.start();


    }
}
