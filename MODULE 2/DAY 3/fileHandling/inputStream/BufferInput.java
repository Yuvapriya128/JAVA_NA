package fileHandling.inputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BufferInput {
    public static void main(String[] args) {
        try(InputStream is=new FileInputStream("file.txt");
            BufferedInputStream bis=new BufferedInputStream(is);
        ){

            int val;
            while((val=bis.read()) != -1){
                System.out.print((char)val);
            }

            System.out.println("Data read");

        }catch (IOException e){
            throw new RuntimeException();
        }
    }
}
