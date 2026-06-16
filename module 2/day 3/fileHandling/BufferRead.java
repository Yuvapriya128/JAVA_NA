package fileHandling;

import java.io.*;

public class BufferRead {
    public static void main(String[] args) {
//        hard disk to ram
        try(Reader fr=new FileReader("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\src\\fileHandling\\myfirstfile.txt");
            BufferedReader br=new BufferedReader(fr);
            ){

           String str;
           do {
               str = br.readLine();
               System.out.println(str);

           }while(str!=null);


        }catch (IOException e){
            throw new RuntimeException();
        }
    }
}
