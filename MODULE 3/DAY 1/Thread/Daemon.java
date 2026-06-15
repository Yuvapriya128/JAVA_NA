package Thread;

class  Mythread extends  Thread{
    private int delay;
    public Mythread(String name,int delay){
        super(name);
        this.delay=delay;
    }
    @Override
    public void run(){
        for(int i=1;i<=10;i++) {
            System.out.println(i + " " + this.getName() + "\n");

            try {
                Thread.sleep(delay);
            } catch (Exception e) {
            }
        }

    }
}


public class Daemon {
    public static void main(String[] args) {
        Thread t1=new Mythread("Sachin",500);
        Thread t2=new Mythread("Yuva",2000);
        Thread t3=new Mythread("Siva",2000);

//        before start ->use setDaemon

        t3.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        t3.start();

//        for(int i=1;i<=10;i++){
//            System.out.println(i+" "+Thread.currentThread().getName()+"\n");
//        }
    }
}
