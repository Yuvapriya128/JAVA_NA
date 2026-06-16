package resourceSharing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;

public class Main {
    public static void main(String[] args) {
        try {
            Thread t1 = new MyThread("Sachin", new FileInputStream("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day10_June15\\src\\Sachin.txt"));
            Thread t2=new MyThread("Saurav",new FileInputStream("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day10_June15\\src\\Saurav.txt"));
            MyThread.opendestwriter();
            t1.start();
            t2.start();

            t1.join();
            t2.join();
//            now close srcreader after join

            MyThread.closedestwriter();

            System.out.println("Exiting main thread");

        }
        catch (FileNotFoundException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
