package fileHandling;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileWrite {
    public static void main(String[] args) {
//        try with resources
//        can add multiple files as i added ;
//        C:\Users\yuvapriya.s\IdeaProjects\Day7_June10\src\stream

//        Writer -> FileWriter is |||r to Animal -> Dog

        /*Java automatically calls:
r.close();
even if an exception occurs.*/

        try(Writer fw=new FileWriter("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\src\\fileHandling\\myfirstfile.txt",true);){
            fw.write("Hello\n");
            fw.write("Bye\n");
            fw.write("Good night\n");
            fw.write("How are you?\n");
            fw.write("Welcome\n");
            fw.write("Have a nice day\n");
            fw.write("See you tomorrow\n");
            fw.write("Take care\n");
            fw.write("Good morning\n");
            fw.write("Thank you\n");
            System.out.println("Data has been appended to the file!");

        }catch (IOException e){
            throw new RuntimeException();
        }


    }
}
