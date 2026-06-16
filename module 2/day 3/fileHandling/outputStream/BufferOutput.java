package fileHandling.outputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BufferOutput {
    public static void main(String[] args) {
        try(OutputStream os=new FileOutputStream("file.txt",true);
            BufferedOutputStream bos=new BufferedOutputStream(os);
        ){
            String data=" High Mountains ";
            bos.write(data.getBytes());



            System.out.println("Data written");


        }catch (IOException e){
            throw new RuntimeException();
        }
    }
}
