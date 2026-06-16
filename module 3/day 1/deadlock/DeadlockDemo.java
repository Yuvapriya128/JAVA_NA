package deadlock;

public class DeadlockDemo {
    public static void main(String[] args) {
        Object chopstick1=new Object();
        Object chopstick2=new Object();

        Thread philosopher1=new Thread(()->{
            System.out.println("Philosopher 1 is occupying chopstick1");

            synchronized (chopstick1){
                System.out.println("Philosopher 1 is occupied chopstick1,going for chopstick2");
//                to create delay -> gives race around condition:::Deadlock
//                sleep ::: needs try catch block
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (chopstick2){
                    System.out.println("Philosopher 1 is eating...");
                }
            }

        });

        Thread philosopher2=new Thread(()->{
            System.out.println("Philosopher 2 is occupying chopstick2");

            synchronized (chopstick2){
                System.out.println("Philosopher 2 is occupied chopstick2,going for chopstick1");
//                to create delay
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (chopstick1){
                    System.out.println("Philosopher 2 is eating...");
                }
            }

        });

        philosopher2.start();
        philosopher1.start();


    }
}
