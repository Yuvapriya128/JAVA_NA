package intro;

import java.util.Scanner;

public class palindrome {
    public static void main(String[] args){
        System.out.println("Enter a word(palidrome):");
        Scanner sc=new Scanner(System.in);
        String str=sc.nextLine();
        str=str.toLowerCase();
        int len=str.length()-1;
        for(int i=0;i<=len/2;) {

            if (str.charAt(i) == str.charAt((len))) {
                i++;len--;
                continue;

            } else {
                System.out.println("Not a palindrome");
                return;
            }

        }
        System.out.println("Palindrome");
    }
}
