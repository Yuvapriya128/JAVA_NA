package maps;

import org.w3c.dom.ls.LSOutput;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class mapsIntro {
    public static void main(String[] args) {
//        Map maintains set of keys
//        Map = {key=value,key=value} pairs
//        keys always be unique and gets overridden
//        set , new is not allowed
//        map , old gets ridden

//        Map<String,String> map=new HashMap<>();
//        Map<String,String> map=new LinkedHashMap<>();

//        TreeMap sorts String so null will result NullPointerException
        Map<String,String> map=new TreeMap<>();
        map.put("fname","john");
        map.put("lname","sparrow");
        map.put("team","black crew");
        map.put("occupation","pirate");

        System.out.println(map);
        System.out.println("-------");
        System.out.println(map.get("fname"));

        System.out.println(map.containsKey("fname"));
        System.out.println(map.containsValue("john"));

        System.out.println("Removing "+map.remove("fname"));
        System.out.println(map.size());
        System.out.println(map.isEmpty());

        map.put("fname","john");
        map.replace("fname","yuva");
        System.out.println(map.get("fname"));



        for(String key: map.keySet()){
            System.out.println(key+" "+map.get(key));
        }
        for(String values: map.values()){
            System.out.println(values);
        }

        System.out.println("----------");

        map.keySet().stream().forEach((key)-> System.out.println(key+" "+map.get(key)));

        map.values().stream().forEach((v)-> System.out.println(v));

        System.out.println("-----------");

        for(Map.Entry<String,String> entry:map.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }

        map.entrySet().stream().forEach((entry)-> System.out.println(entry.getKey()+" "+entry.getValue()));

//        map -> has forEach from java8 onwards
        map.forEach((key,value)-> System.out.println(key+" "+map.get(key)));

        System.out.println("-------------");
// stream works then all functions of stream should work , try once.


    }

}
