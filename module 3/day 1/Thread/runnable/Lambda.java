package Thread.runnable;

public class Lambda {
    public static void main(String[] args) {
        for(int i=1;i<=3;i++) {
            new Thread(()->{
                for(int j=1;j<=3;j++){
                    System.out.println(j+" "+Thread.currentThread().getName());
                }
            }).start();
        }
        System.out.println("Exiting main thread");
    }
}
