package fileHandling.outputStream;

import java.io.*;

public class outputStreamIntro {
    public static void main(String[] args) {
        try(OutputStream os=new FileOutputStream("file.txt")){
            String data=" High Mountains ";
            os.write(data.getBytes());

            System.out.println("Data written");


        }catch (IOException e){
            throw new RuntimeException();
        }
    }
}
