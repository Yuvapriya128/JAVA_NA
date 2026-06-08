package Overloading;

public class Calculator {
     public int add(int a,int b){
         System.out.println("Integer is called");
        return a+b;

    }

    public short add(short a,short b){
        System.out.println("Short is called");
        return (short)(a+b);

    }
    public long add(long a,long b){
        System.out.println("long is called");
        return a+b;

    }
    public byte add(byte a,byte b){
        System.out.println("byte is called");
        return (byte)(a+b);

    }
    public float add(float a,float b){
        System.out.println("Float is called");
        return a+b;

    }
    public double add(double a,double b){
        System.out.println("Double is called");
        return (a+b);

    }

    public int add(int a,int b,int c){
         return a+b+c;
    }
    public String add(String a,String b){
         return a+b;
    }


    public static void main(String[] args) {
        Calculator cal=new Calculator();
        byte a=3;
        byte b=4;


        System.out.println(cal.add(a,b));

        System.out.println(cal.add(7.2,3f));
        System.out.println(cal.add("yuva","priya"));
        System.out.println(cal.add(3,45,5));

    }
}
