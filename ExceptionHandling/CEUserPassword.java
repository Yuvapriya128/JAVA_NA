package ExceptionHandling;

import java.util.Scanner;

public class CEUserPassword {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter your name:");
        String str=sc.next();
        System.out.println("Enter your password:");
        String strpass=sc.next();

        try {
            if(!str.equalsIgnoreCase("yuva") && !strpass.equalsIgnoreCase("yuva@1")){
                throw new InvalidCredentials("Invalid Credentials.Try again");
            }
            System.out.println("User and password is correct");
        }catch (InvalidCredentials e){
            System.out.println("Enter correct credentials\n  "+e.getMessage());
        }
    }
}
