package Thread.runnable;

/*This is functional interface
* should've done lambda function
* */
public class MyRunnable implements Runnable {

    @Override
    public void run() {
        for(int i=1;i<=10;i++)
        System.out.println(i+" "+Thread.currentThread().getName()+" \n");
    }
}
