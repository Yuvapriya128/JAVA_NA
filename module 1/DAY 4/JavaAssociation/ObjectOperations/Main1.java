package ObjectOperations;

public class Main1 {
    public static void main(String[] args) {
        Object o1=new Object();
        Object o2=new Object();
        Object o3=o1;

        System.out.println(o1==o2); //false as they created in heap
        System.out.println(o1==o3); //true as they refers same

//        equals works the same as ==
        System.out.println(o1.hashCode());
//        objects can have same or different hashCodes comparing the buckets
        System.out.println(o1.getClass().getSimpleName());
        System.out.println(o1.getClass().getName());
    }
}
