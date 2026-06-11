package fileHandling;

import java.io.*;

public class Copying {
    public static void main(String[] args) {
        try(Reader fr=new FileReader("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\src\\fileHandling\\myfirstfile.txt");
            BufferedReader br=new BufferedReader(fr);
            Writer fw=new FileWriter("C:\\Users\\yuvapriya.s\\IdeaProjects\\Day7_June10\\src\\fileHandling\\demoCopy.txt");
            BufferedWriter bw=new BufferedWriter(fw);
            ){
            String line;
            do{
                line=br.readLine();
                if(line!=null)
                bw.write(line);
                bw.newLine();
            }while(line!=null);


        }catch (IOException e){
            throw new RuntimeException();
        }

    }
}
