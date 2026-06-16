package fileHandling;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class BufferWrite {
    public static void main(String[] args) {
        try(Writer fw=new FileWriter("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\src\\fileHandling\\mySecfile.txt");
            BufferedWriter bw=new BufferedWriter(fw);
            ){

            bw.write("Hello this is buffered writer");
            bw.newLine();
            bw.write("buffered bye byee");


        }catch (IOException e){
            throw new RuntimeException();
        }

    }
}

