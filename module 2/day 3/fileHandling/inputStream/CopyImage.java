package fileHandling.inputStream;

import java.io.*;

public class CopyImage {
    public static void main(String[] args) {
        try(OutputStream os=new FileOutputStream("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\copy.jpg");
            BufferedOutputStream bos=new BufferedOutputStream(os);
            InputStream is=new FileInputStream("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\cancelled cheque.jpg");
            BufferedInputStream bis=new BufferedInputStream(is);
        ){
            int val;
            while((val=bis.read()) != -1){
                bos.write(val);
            }

        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
