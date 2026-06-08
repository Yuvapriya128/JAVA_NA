package ExceptionHandling;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
//        Task 1
        try {
            System.out.println("Enter a:");
            int a = sc.nextInt();
            System.out.println("Enter b:");
            int b = sc.nextInt();
            int res = a / b;
            String s = null;
            System.out.println(s);
        }
        catch (ArithmeticException a){
            System.out.println("Division by 0 not allowed");
        }catch (InputMismatchException e){
            System.out.println("int should be given");
        }catch (Exception e){
            System.out.println("Something went wrong: "+e);
        }finally{
            System.out.println("Scanner closed");
            sc.close();
        }

//        Finally executes whether exception works or not

//        consider this Task 2
//        Only after specific exceptions i should print generic exceptions

        System.out.println("Hello Lava");
    }
}
