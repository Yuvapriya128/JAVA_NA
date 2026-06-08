package ExceptionHandling;

import java.util.Scanner;

public class CustomeException {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter your name:");
        String str=sc.next();
        try {
            if (!str.equalsIgnoreCase("siva") && !str.equalsIgnoreCase("priya") && !str.equalsIgnoreCase("gokul")) {
                throw new NotFound("Invalid name");
            }
            System.out.println("Allowed");
        }catch(NotFound e){
            System.out.println("Not allowed "+e.getMessage());
        }
    }
}
