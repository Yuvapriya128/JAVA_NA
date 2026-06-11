package fileHandling.inputStream;

import java.io.*;

public class Copying {
    public static void main(String[] args) {
        try(OutputStream os=new FileOutputStream("newfile.txt");
            BufferedOutputStream bos=new BufferedOutputStream(os);
            InputStream is=new FileInputStream("file.txt");
            BufferedInputStream bis=new BufferedInputStream(is);
            ){
            int val;
            while((val=bis.read()) != -1){
                bos.write((char)val);
            }

        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
