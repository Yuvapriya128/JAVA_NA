package ObjectStream;

import java.io.*;

public class WriteObject {
    public void write(File filename,Object obj){
        try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(obj);

            System.out.println("Object is written");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
