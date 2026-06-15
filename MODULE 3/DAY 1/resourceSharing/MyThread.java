package resourceSharing;

import java.io.*;

public class MyThread extends Thread{

//    bcoz of private static we used methods to access them

    private static OutputStream destwriter;

//    reader should be non-static
/*
* Since srcreader is static:

t1 sets srcreader → Sachin.txt stream
t2 sets srcreader → Saurav.txt stream (overwrites the first one)

Now both threads are using the same InputStream object (Saurav.txt).
* */

    private  InputStream srcreader;

    public MyThread(String name,InputStream srcreader){
        super(name);
        this.srcreader=srcreader;

    }
    public static void opendestwriter() throws FileNotFoundException{
        MyThread.destwriter=new FileOutputStream("Output.log");
    }
    public static void closedestwriter() throws IOException{
        destwriter.close();
    }

    @Override
    public void run(){
        synchronized (destwriter) {
            System.out.println("Thread name:" + Thread.currentThread().getName() + "\n");
            int c;
            try {
                while ((c = srcreader.read()) != -1) {
                    destwriter.write(c);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//        close srcreader
            finally {
                try {
                    srcreader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }
}
