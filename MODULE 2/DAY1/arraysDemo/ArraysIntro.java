package arraysDemo;

import java.util.Arrays;
import java.util.Scanner;

public class ArraysIntro {
    public static void main(String[] args) {

        System.out.printf("int arrays!");
        int[] intarr=new int[5];
        for (int i = 0; i <= 4; i++) {
           intarr[i]=i;
        }
        for(int i=0;i<=4;i++){
            System.out.print(intarr[i]+" ");
        }
        System.out.println("Length of intarr: "+intarr.length);

        System.out.println("short arrays");
        short[] shortarr=new short[5];
        for(int i=0;i<=4;i++){
            shortarr[i]=(short) i;
        }
        for(int i=0;i<=4;i++){
            System.out.print(shortarr[i]+" ");
        }
        System.out.println("Length of shortarr: "+shortarr.length);

        System.out.println("byte arrays");
        byte[] bytearr=new byte[5];
        for(int i=0;i<=4;i++){
            bytearr[i]=(byte) i;
        }
        for(int i=0;i<=4;i++){
            System.out.print(bytearr[i]+" ");
        }
        System.out.println("Length of bytearr: "+bytearr.length);

        System.out.println("char arrays");
        char[] chararr={'s','b','c'};


        for(char c: chararr){
            System.out.print(c);
        }
        System.out.println("\nLength of chararr: "+chararr.length);

        System.out.println("String arrays");
        String[] strarr=new String[5];
        for(int i=0;i<=4;i++){
            strarr[i]="hello";
        }

        for(String i:strarr)
        System.out.print(i+" ");
        System.out.println("Length of strarr: "+strarr.length);

        System.out.println("Enter your name:");
        Scanner sc=new Scanner(System.in);
        String[] guests={"sachin","saurav","yuvaraj","rahul"};
        String strentered=sc.nextLine();
        int flag=0;
        try{
        for(String guest:guests) {
            if (strentered.equalsIgnoreCase(guest)) {
                System.out.println("WELCOME " + strentered);
                flag = 1;
            }
        }
        if(flag==0) {
            System.out.println("You are not in the list");
            throw new NameNotFound("Not found ");
        }}catch (NameNotFound msg){
            System.out.println("Error msg: "+msg);
        }


//        Sorting
        for(String i:strarr)
            System.out.print(i+" ");
        Arrays.sort(strarr);

        int[] arr={3,4,2,5};
        Arrays.sort(arr);
        for(int val:arr){
            System.out.print(val+" ");
        }

//        swap 2 values in an array
        System.out.println("Swapping 2 values");
        for(int val:intarr){
            System.out.print(val+" ");
        }

        int temp=intarr[1];
        intarr[1]=intarr[intarr.length-1];
        intarr[intarr.length-1]=temp;
        System.out.println("\nswapped");
        for(int val:intarr){
            System.out.print(val+" ");
        }

//        reverse of an array
        System.out.println("\nReversing array\n");
        for(int val:intarr){
            System.out.print(val+" ");
        }
        int len= intarr.length;
        for(int i=0;i<len/2;i++){
            int tem=intarr[i];
            intarr[i]=intarr[len-1-i];
            intarr[len-1]=tem;
        }
        System.out.println("\nReversed array\n");
        for(int val:intarr){
            System.out.print(val+" ");
        }

//        Wrapper class sorting
        Integer[] objint={1,2,42,44,2};
        System.out.println(Arrays.toString(objint));
        Arrays.sort(objint);
        System.out.println(Arrays.toString(objint));




    }
}