package fileHandling.inputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class inputStreamIntro {
    public static void main(String[] args) {
        try(InputStream is=new FileInputStream("file.txt")){

            int val;
            while((val=is.read()) != -1){
                System.out.print((char)val);
            }

        }catch (IOException e){
            throw new RuntimeException();
        }
    }
}
