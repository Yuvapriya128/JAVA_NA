package ObjectStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class ReadObject {
    public void read(File filename){
        try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(filename))){
            Person p=(Person)ois.readObject();

            System.out.println(p);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
