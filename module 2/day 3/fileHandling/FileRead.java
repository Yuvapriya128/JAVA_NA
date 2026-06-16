package fileHandling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileRead {
    public static void main(String[] args) {
        try(Reader fr=new FileReader("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\src\\fileHandling\\myfirstfile.txt");){
            int val;
            do{
                val=fr.read();
                if(val!=-1)
                System.out.print((char)val);
            }while(val!=-1);

        }catch (FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
