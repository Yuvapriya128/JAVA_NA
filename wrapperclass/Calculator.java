package WrapperClass;

public class Calculator {
    public static int add(int a,int b){
        System.out.println("int");
        return a+b;
    }
    public static long add(long a,long b){
        System.out.println("long");
        return a+b;
    }
    public static double add(double a,double b){
        System.out.println("double");
        return a+b;
    }
    public static short add(short a,short b){
        System.out.println("short");
        return (short)(a+b);
    }
    public static Integer add(Integer a,Integer b){
        System.out.println("Integer");
        return a+b;
    }
    public static void main(String[] args) {
        Calculator cal=new Calculator();
        System.out.println(cal.add(5,4));
    }
}
//Auto widening happens before autoboxing
//byte->short->int->long->float->double
//Autoboxing happens